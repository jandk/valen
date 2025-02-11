package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.event.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.events.*;
import jakarta.inject.*;
import javafx.application.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public final class MainPresenter extends AbstractFXPresenter<MainView> {
    private final SendChannel<MainEvent> channel;
    private final FileListPresenter fileList;
    private final Settings settings;

    private @Nullable Game game;
    private @Nullable Archive archive;
    private @Nullable Asset lastAsset;
    private String query = "";

    @Inject
    MainPresenter(
        MainView view,
        EventBus eventBus,
        FileListPresenter fileList,
        // SettingsPresenter settingsPresenter,
        Settings settings
    ) {
        super(view);

        this.channel = eventBus.senderFor(MainEvent.class);
        this.fileList = fileList;
        this.settings = settings;

        eventBus
            .receiverFor(MainViewEvent.class)
            .consume(event -> {
                switch (event) {
                    case MainViewEvent.ArchiveSelected(var name) -> selectArchive(name);
                    case MainViewEvent.PreviewVisibilityChanged(var visible) -> showPreview(visible);
                    case MainViewEvent.SettingVisibilityChanged(var visible) -> showSettings(visible);
                    case MainViewEvent.LoadGameClicked _ -> channel.send(new MainEvent.GameLoadRequested());
                    case MainViewEvent.ExportClicked() -> exportSelectedAsset();
                    case MainViewEvent.SearchChanged(var query) -> {
                        this.query = query;
                        updateFileList();
                    }
                }
            });

        eventBus
            .receiverFor(AssetSelected.class)
            .consume(event -> selectAsset(event.asset()));

        eventBus
            .receiverFor(SettingsApplied.class)
            .consume(_ -> updateFileList());
    }

    private void selectArchive(String archiveName) {
        if (game == null) {
            return;
        }
        try {
            archive = game.loadArchive(archiveName);
            updateFileList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void selectAsset(Asset asset) {
        if (getView().isSidePaneVisible() && archive != null) {
            try {
                var type = asset.type() == AssetType.BINARY ? byte[].class : Object.class;
                var assetData = archive.loadAsset(asset.id(), type);
                Platform.runLater(() -> getView().setupPreview(asset, assetData));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        lastAsset = asset;
    }

    private void showPreview(boolean visible) {
        if (visible && lastAsset != null) {
            selectAsset(lastAsset);
        }
    }

    private void showSettings(boolean visible) {
    }

    private void exportSelectedAsset() {
        var asset = fileList.getSelectedAsset();
        if (asset == null) {
            return;
        }

        // TODO: Clean this up
        var extension = Exporter.forType(asset.type().getType()).stream().findFirst().orElseThrow().getExtension();
        var filename = extension.isEmpty()
            ? asset.id().fileName()
            : Filenames.removeExtension(asset.id().fileName()) + "." + extension;

        channel.send(new MainEvent.SaveFileRequested(filename, path -> {
            getView().setExporting(true);

            var exportTask = new ExportTask(path, archive, asset);
            CompletableFuture
                .runAsync(exportTask::export0)
                .handle((_, _) -> {
                    getView().setExporting(false);
                    return null;
                });
        }));
    }

    private void updateFileList() {
        if (archive == null) {
            return;
        }
        var predicate = buildPredicate(query, settings.assetTypes().get().orElse(Set.of()));
        var assets = archive.assets().filter(predicate);
        fileList.setAssets(assets);
    }

    private Predicate<Asset> buildPredicate(String assetName, Collection<AssetType> assetTypes) {
        List<Predicate<Asset>> predicates = new ArrayList<>();
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
        getView().setArchives(game.archiveNames());
    }

    public void focusOnSearch() {
        getView().focusOnSearch();
    }
}
