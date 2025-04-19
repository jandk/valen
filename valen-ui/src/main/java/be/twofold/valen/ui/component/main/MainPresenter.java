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

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class MainPresenter extends AbstractFXPresenter<MainView> {
    private final Logger log = LoggerFactory.getLogger(MainPresenter.class);

    private final ExportService exportService;
    private final FileListPresenter fileList;
    private final Settings settings;

    private @Nullable Game game;
    private @Nullable Archive<AssetID, Asset> archive;
    private @Nullable Asset lastAsset;
    private String query = "";

    @Inject
    MainPresenter(
        MainView view,
        EventBus eventBus,
        FileListPresenter fileList,
        Settings settings,
        ExportService exportService
    ) {
        super(view);

        this.fileList = fileList;
        this.settings = settings;
        this.exportService = exportService;

        eventBus.subscribe(MainViewEvent.class, event -> {
            switch (event) {
                case MainViewEvent.ArchiveSelected(var name) -> selectArchive(name);
                case MainViewEvent.PreviewVisibilityChanged(var visible) -> showPreview(visible);
                case MainViewEvent.SettingVisibilityChanged(var visible) -> showSettings(visible);
                case MainViewEvent.LoadGameClicked _ -> eventBus.publish(new MainEvent.GameLoadRequested());
                case MainViewEvent.ExportClicked() -> exportSelectedAssets();
                case MainViewEvent.SearchChanged(var query) -> {
                    this.query = query;
                    updateFileList();
                }
            }
        });

        eventBus.subscribe(AssetSelected.class, event -> selectAsset(event.asset(), event.forced()));
        eventBus.subscribe(SettingsApplied.class, _ -> updateFileList());
        eventBus.subscribe(ExportRequested.class, event -> exportPath(event.path(), event.recursive()));

        exportService.progressProperty().addListener((_, _, newValue) -> {
            getView().setProgress(newValue.doubleValue());
        });
        exportService.messageProperty().addListener((_, _, newValue) -> {
            getView().setProgressMessage(newValue);
        });
        exportService.stateProperty().addListener((_, _, newValue) -> {
            getView().setExporting(newValue == Worker.State.RUNNING);
        });
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
                    case MODEL, TEXTURE -> asset.type().getType();
                    default -> ByteBuffer.class;
                };
                var assetData = archive.loadAsset(asset.id(), type);
                Platform.runLater(() -> getView().setupPreview(asset, assetData));
            } catch (IOException e) {
                log.error("Could not load asset{}", asset.id().fileName(), e);
                FxUtils.showExceptionDialog(e, "Could not load asset" + asset.id().fileName());
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
        var predicate = buildPredicate(query, settings.assetTypes().get().orElse(Set.of()));
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
