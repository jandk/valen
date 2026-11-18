package be.twofold.valen.ui.component.main;

import backbonefx.event.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.events.*;
import be.twofold.valen.ui.service.*;
import jakarta.inject.*;
import org.slf4j.*;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

public final class MainPresenter extends AbstractPresenter<MainView> implements MainView.Listener {
    private final Logger log = LoggerFactory.getLogger(MainPresenter.class);

    private final FileListPresenter fileList;

    private final Session session;
    private final Settings settings;

    private final GameService gameService;
    private final ExportService exportService;
    private final Dialogs dialogs;

    // Loads run off the FX thread so browsing stays responsive. A single thread
    // serializes loads; loadSeq makes the latest selection win (see selectAsset).
    private final ExecutorService loadExecutor = Executors.newSingleThreadExecutor(runnable -> {
        var thread = new Thread(runnable, "asset-loader");
        thread.setDaemon(true);
        return thread;
    });
    private final AtomicLong loadSequence = new AtomicLong();

    private Asset lastAsset;
    private SidePanel sidePanel = SidePanel.NONE;
    private String query = "";

    @Inject
    MainPresenter(
        MainView view,
        FileListPresenter fileList,
        Session session,
        Settings settings,
        GameService gameService,
        ExportService exportService,
        Dialogs dialogs,
        UiThread uiThread,
        EventBus eventBus
    ) {
        super(view);

        this.fileList = fileList;
        this.session = session;
        this.settings = settings;
        this.gameService = gameService;
        this.exportService = exportService;
        this.dialogs = dialogs;

        view.setListener(this);
        view.setFileListView(fileList.getView().getFXNode());

        session.gameProperty().subscribe(game -> uiThread.execute(() -> gameChanged(game)));
        session.loaderProperty().subscribe(_ -> uiThread.execute(this::loaderChanged));
        session.installedGamesProperty().subscribe(games -> uiThread.execute(() -> getView().setGames(games)));

        eventBus.subscribe(AssetSelected.class, event -> selectAsset(event.asset(), event.forced()));
        eventBus.subscribe(SettingsApplied.class, _ -> applySettings());
        eventBus.subscribe(ExportRequested.class, event -> exportPath(event.path(), event.recursive()));

        exportService.progressProperty().subscribe(progress -> uiThread.execute(() -> getView().setExporting(progress != null)));
    }

    @Override
    public void onGameSelected(Path executable) {
        gameService.load(executable);
    }

    @Override
    public void onArchiveSelected(String name) {
        gameService.openArchive(name);
    }

    @Override
    public void onSidePanelToggled(SidePanel panel) {
        sidePanel = panel;
        getView().showSidePanel(panel);
        if (panel == SidePanel.PREVIEW) {
            if (lastAsset != null) {
                selectAsset(lastAsset, false);
            }
        } else {
            // Leaving the preview: drop any in-flight load and its spinner.
            cancelPreviewLoad();
        }
    }

    @Override
    public void onLoadGameClicked() {
        gameService.browse();
    }

    @Override
    public void onExportClicked() {
        exportSelectedAssets();
    }

    @Override
    public void onSearchChanged(String query) {
        MainPresenter.this.query = query;
        updateFileList();
    }

    public void focusOnSearch() {
        getView().focusOnSearch();
    }

    private void gameChanged(Session.LoadedGame game) {
        getView().setCurrentGame(game != null ? game.entry() : null);
        getView().setArchives(game != null
            ? game.game().archiveNames().stream().sorted().toList()
            : List.of());
    }

    private void loaderChanged() {
        resetSelection();
        updateFileList();
    }

    private void resetSelection() {
        lastAsset = null;
        cancelPreviewLoad();
        getView().clearPreview();
    }

    private void selectAsset(Asset asset, boolean forced) {
        if (forced) {
            sidePanel = SidePanel.PREVIEW;
            getView().showSidePanel(SidePanel.PREVIEW);
        }
        lastAsset = asset;

        // Bump the sequence so any in-flight load is superseded, even if we
        // don't start a new one (e.g. the preview panel is hidden).
        var seq = loadSequence.incrementAndGet();
        var currentLoader = session.getLoader();
        if (sidePanel != SidePanel.PREVIEW || currentLoader == null) {
            return;
        }

        getView().setPreviewLoading(true);
        loadExecutor.execute(() -> loadAsset(seq, currentLoader, asset));
    }

    private void loadAsset(long seq, AssetLoader loader, Asset asset) {
        try {
            var type = previewType(asset);
            var assetData = loader.load(asset.id(), type.type());
            Meta.Node metadata = type != AssetType.RAW
                ? loader.loadMetadata(asset.id()).orElse(null)
                : null;
            if (isStale(seq)) {
                return;
            }
            var preview = getView().decodePreview(type, assetData, metadata);
            if (isStale(seq)) {
                return;
            }
            getView().displayPreview(preview);
            getView().setPreviewLoading(false);
        } catch (Exception e) {
            if (isStale(seq)) {
                return;
            }
            log.error("Could not load asset {}", asset.id().path().fileName(), e);
            getView().setPreviewLoading(false);
            dialogs.showException("Could not load asset " + asset.id().path().fileName(), e);
        }
    }

    private AssetType previewType(Asset asset) {
        if (settings.isTreatAsRaw()) {
            return AssetType.RAW;
        }
        return switch (asset.type()) {
            case AUDIO, MODEL, TEXTURE -> asset.type();
            default -> AssetType.RAW;
        };
    }

    /**
     * A newer selection has superseded this load, so its result should be
     * dropped. The superseding selection owns the spinner from here on.
     */
    private boolean isStale(long seq) {
        return seq != loadSequence.get();
    }

    private void cancelPreviewLoad() {
        loadSequence.incrementAndGet();
        getView().setPreviewLoading(false);
    }

    private void exportSelectedAssets() {
        var assets = fileList.getView().getSelectedAssets();
        exportAssets(assets);
    }

    private void exportPath(AssetPath path, boolean recursive) {
        Predicate<AssetPath> predicate = recursive ? s -> s.startsWith(path) : s -> s.equals(path);
        var assets = filteredAssets()
            .filter(asset -> predicate.test(asset.id().path().parent()))
            .toList();

        exportAssets(assets);
    }

    private void exportAssets(List<? extends Asset> assets) {
        if (assets.isEmpty()) {
            return;
        }

        exportService.export(session.getLoader(), assets);
    }

    private void applySettings() {
        updateFileList();

        // "Treat as raw" changes how the current asset decodes, so reload it.
        if (lastAsset != null && sidePanel == SidePanel.PREVIEW) {
            selectAsset(lastAsset, false);
        }
    }

    private void updateFileList() {
        fileList.setAssets(filteredAssets());
    }

    private Stream<? extends Asset> filteredAssets() {
        var loader = session.getLoader();
        if (loader == null) {
            return Stream.empty();
        }

        // Nothing is decoded in raw mode, so the type filter doesn't apply: list everything.
        var assetTypes = settings.isTreatAsRaw() ? Set.<AssetType>of() : settings.getAssetTypes();
        var predicate = buildPredicate(query, assetTypes);
        return loader.all().filter(predicate);
    }

    private Predicate<Asset> buildPredicate(String assetName, Collection<AssetType> assetTypes) {
        var predicates = new ArrayList<Predicate<Asset>>();
        if (assetName != null) {
            predicates.add(asset -> asset.id().fullName().contains(assetName));
        }
        if (assetTypes != null && !assetTypes.isEmpty()) {
            var assetTypeSet = EnumSet.copyOf(assetTypes);
            predicates.add(asset -> assetTypeSet.contains(asset.type()));
        }
        return predicates.stream()
            .reduce(Predicate::and)
            .orElse(_ -> true);
    }
}
