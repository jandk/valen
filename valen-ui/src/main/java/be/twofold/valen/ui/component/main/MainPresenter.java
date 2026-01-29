package be.twofold.valen.ui.component.main;

import backbonefx.event.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.events.*;
import jakarta.inject.*;
import javafx.application.*;
import javafx.concurrent.*;
import org.jetbrains.annotations.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class MainPresenter extends AbstractPresenter<MainView> implements MainView.Listener {
    private final Logger log = LoggerFactory.getLogger(MainPresenter.class);

    private final ExportService exportService;
    private final FileListPresenter fileList;
    private final Settings settings;
    private final EventBus eventBus;

    private @Nullable Game game;
    private @Nullable Archive<AssetID, Asset> archive;
    private @Nullable Asset lastAsset;
    private String query = "";

    @Inject
    MainPresenter(
        MainView view,
        EventBus eventBus,
        Settings settings,
        ExportService exportService,
        ViewLoader viewLoader
    ) {
        super(view);

        this.fileList = viewLoader.loadPresenter(
            FileListPresenter.class,
            "/fxml/FileList.fxml"
        );
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
    public void onPreviewVisibilityChanged(boolean visible) {
        showPreview(visible);
    }

    @Override
    public void onSettingsVisibilityChanged(boolean visible) {
        showSettings(visible);
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

    @SuppressWarnings("unchecked")
    private void selectArchive(String archiveName) {
        if (game == null) {
            return;
        }
        try {
            // This cast is safe here, we don't care about the actual types
            archive = (Archive<AssetID, Asset>) game.loadArchive(archiveName);
            updateFileList();
        } catch (IOException e) {
            log.error("Could not load archive {}", archiveName, e);
            FxUtils.showExceptionDialog(e, "Could not load archive " + archiveName);
        }
    }

    private void selectAsset(Asset asset, boolean forced) {
        if (forced) {
            Platform.runLater(() -> getView().showPreview(true));
        }
        if (getView().isSidePaneVisible() && archive != null) {
            try {
                var type = switch (asset.type()) {
                    case MODEL, TEXTURE -> asset.type().type();
                    default -> Bytes.class;
                };
                var assetData = archive.loadAsset(asset.id(), type);
                Platform.runLater(() -> getView().setupPreview(asset, assetData));
            } catch (IOException e) {
                log.error("Could not load asset {}", asset.id().fileName(), e);
                FxUtils.showExceptionDialog(e, "Could not load asset " + asset.id().fileName());
            }
        }
        lastAsset = asset;
    }

    private void showPreview(boolean visible) {
        if (visible && lastAsset != null) {
            selectAsset(lastAsset, false);
        }
    }

    private void showSettings(boolean visible) {
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

    private void exportAssets(List<Asset> assets) {
        if (assets.isEmpty()) {
            return;
        }

        Platform.runLater(() -> {
            exportService.setArchive(archive);
            exportService.setAssets(assets);
            exportService.restart();
        });
    }

    private void updateFileList() {
        fileList.setAssets(filteredAssets());
    }

    private Stream<Asset> filteredAssets() {
        if (archive == null) {
            return Stream.empty();
        }
        var predicate = buildPredicate(query, settings.getAssetTypes());
        return archive.getAll().filter(predicate);
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
