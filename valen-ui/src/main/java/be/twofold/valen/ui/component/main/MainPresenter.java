package be.twofold.valen.ui.component.main;

import backbonefx.di.*;
import backbonefx.event.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.events.*;
import jakarta.inject.*;
import javafx.concurrent.*;
import org.jetbrains.annotations.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

public final class MainPresenter extends AbstractPresenter<MainView> implements MainView.Listener {
    private final Logger log = LoggerFactory.getLogger(MainPresenter.class);

    private final ExportService exportService;
    private final FileListPresenter fileList;
    private final Settings settings;
    private final EventBus eventBus;

    // Loads run off the FX thread so browsing stays responsive. A single thread
    // serializes loads; loadSeq makes the latest selection win (see selectAsset).
    private final ExecutorService loadExecutor = Executors.newSingleThreadExecutor(runnable -> {
        var thread = new Thread(runnable, "asset-loader");
        thread.setDaemon(true);
        return thread;
    });
    private final AtomicLong loadSequence = new AtomicLong();

    private @Nullable Game game;
    private AssetLoader loader;
    private @Nullable Asset lastAsset;
    private SidePanel sidePanel = SidePanel.NONE;
    private String query = "";

    @Inject
    MainPresenter(
        MainView view,
        EventBus eventBus,
        Settings settings,
        ExportService exportService,
        Feather feather
    ) {
        super(view);

        this.fileList = feather.instance(FileListPresenter.class);
        this.settings = settings;
        this.exportService = exportService;
        this.eventBus = eventBus;

        view.setListener(this);
        view.setFileListView(fileList.getView().getFXNode());

        eventBus.subscribe(AssetSelected.class, event -> selectAsset(event.asset(), event.forced()));
        eventBus.subscribe(SettingsApplied.class, _ -> updateFileList());
        eventBus.subscribe(ExportRequested.class, event -> exportPath(event.path(), event.recursive()));

        exportService.stateProperty().addListener((_, _, newValue) -> {
            getView().setExporting(newValue == Worker.State.RUNNING);
        });
    }

    @Override
    public void onArchiveSelected(String name) {
        selectArchive(name);
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
        eventBus.publish(new MainEvent.GameLoadRequested());
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

    private void selectArchive(String archiveName) {
        if (game == null) {
            return;
        }
        try {
            // This cast is safe here, we don't care about the actual types
            loader = game.open(archiveName);
            cancelPreviewLoad();
            updateFileList();
        } catch (IOException e) {
            log.error("Could not load archive {}", archiveName, e);
            FxUtils.showExceptionDialog(e, "Could not load archive " + archiveName);
        }
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
        if (sidePanel != SidePanel.PREVIEW || loader == null) {
            return;
        }

        var currentLoader = loader;
        getView().setPreviewLoading(true);
        loadExecutor.submit(() -> loadAsset(seq, currentLoader, asset));
    }

    private void loadAsset(long seq, AssetLoader loader, Asset asset) {
        try {
            var type = switch (asset.type()) {
                case MODEL, TEXTURE -> asset.type().type();
                default -> Bytes.class;
            };
            var assetData = loader.load(asset.id(), type);
            var metadata = loader.loadMetadata(asset.id()).orElse(null);
            if (isStale(seq)) {
                return;
            }
            var preview = getView().decodePreview(asset.type(), assetData, metadata);
            if (isStale(seq)) {
                return;
            }
            getView().displayPreview(preview);
            getView().setPreviewLoading(false);
        } catch (IOException e) {
            if (isStale(seq)) {
                return;
            }
            log.error("Could not load asset {}", asset.id().fileName(), e);
            getView().setPreviewLoading(false);
            FxUtils.showExceptionDialog(e, "Could not load asset " + asset.id().fileName());
        }
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

    private void exportPath(String path, boolean recursive) {
        Predicate<String> predicate = recursive ? s -> s.startsWith(path) : s -> s.equals(path);
        var assets = filteredAssets()
            .filter(asset -> predicate.test(asset.id().pathName()))
            .toList();

        exportAssets(assets);
    }

    private void exportAssets(List<? extends Asset> assets) {
        if (assets.isEmpty()) {
            return;
        }

        exportService.export(loader, assets);
    }

    private void updateFileList() {
        fileList.setAssets(filteredAssets());
    }

    private Stream<? extends Asset> filteredAssets() {
        if (loader == null) {
            return Stream.empty();
        }
        var predicate = buildPredicate(query, settings.getAssetTypes());
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

    public void setGame(Game game) {
        this.game = game;
        getView().setArchives(game.archiveNames().stream().sorted().toList());
    }

    public void focusOnSearch() {
        getView().focusOnSearch();
    }
}
