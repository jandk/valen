package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.component.preview.*;
import be.twofold.valen.ui.component.settings.*;
import be.twofold.valen.ui.service.*;
import jakarta.inject.*;
import org.slf4j.*;

import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class MainPresenter extends AbstractPresenter<MainView> implements MainView.Listener, FileListPresenter.Listener {
    private final Logger log = LoggerFactory.getLogger(MainPresenter.class);

    private final FileListPresenter fileListPresenter;
    private final PreviewPresenter previewPresenter;
    private final SettingsPresenter settingsPresenter;

    private final Session session;
    private final Settings settings;

    private final GameService gameService;
    private final ExportService exportService;

    private String query = "";

    @Inject
    MainPresenter(
        MainView view,
        FileListPresenter fileListPresenter,
        PreviewPresenter previewPresenter,
        SettingsPresenter settingsPresenter,
        Session session,
        Settings settings,
        GameService gameService,
        ExportService exportService,
        UiThread uiThread
    ) {
        super(view);

        this.fileListPresenter = fileListPresenter;
        this.fileListPresenter.setListener(this);

        this.previewPresenter = previewPresenter;

        this.settingsPresenter = settingsPresenter;
        this.settingsPresenter.setOnApplied(this::applySettings);

        this.session = session;
        this.settings = settings;
        this.gameService = gameService;
        this.exportService = exportService;

        view.setListener(this);
        view.setFileListView(fileListPresenter.getView());
        view.setPreviewView(previewPresenter.getView());
        view.setSettingsView(settingsPresenter.getView());

        session.gameProperty().subscribe(game -> uiThread.execute(() -> gameChanged(game)));
        session.loaderProperty().subscribe(_ -> uiThread.execute(this::loaderChanged));
        session.installedGamesProperty().subscribe(games -> uiThread.execute(() -> getView().setGames(games)));

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
        getView().showSidePanel(panel);
        previewPresenter.setVisible(panel == SidePanel.PREVIEW);
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

    @Override
    public void onAssetSelected(Asset asset, boolean forced) {
        // Set the asset first, so opening the panel loads it once instead of the previous one
        previewPresenter.setAsset(asset);
        if (forced) {
            getView().showSidePanel(SidePanel.PREVIEW);
            previewPresenter.setVisible(true);
        }
    }

    @Override
    public void onPathExportRequested(AssetPath path, boolean recursive) {
        Predicate<AssetPath> predicate = recursive ? s -> s.startsWith(path) : s -> s.equals(path);
        var assets = filteredAssets()
            .filter(asset -> predicate.test(asset.id().path().parent()))
            .toList();

        exportAssets(assets);
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
        updateFileList();
    }


    private void exportSelectedAssets() {
        var assets = fileListPresenter.getView().getSelectedAssets();
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
        previewPresenter.reload();
    }

    private void updateFileList() {
        fileListPresenter.setAssets(filteredAssets());
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
