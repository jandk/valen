package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.event.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.events.*;
import jakarta.inject.*;
import javafx.application.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public final class MainPresenter extends AbstractFXPresenter<MainView> {
    private final SendChannel<MainEvent> channel;
    private final FileListPresenter fileList;

    private Game game;
    private Archive archive;
    private Asset lastAsset;


    @Inject
    MainPresenter(MainView view, FileListPresenter fileList, EventBus eventBus) {
        super(view);

        this.channel = eventBus.senderFor(MainEvent.class);
        this.fileList = fileList;

        eventBus
            .receiverFor(MainViewEvent.class)
            .consume(event -> {
                switch (event) {
                    case MainViewEvent.ArchiveSelected(var name) -> selectArchive(name);
                    case MainViewEvent.PreviewVisibilityChanged(var visible) -> showPreview(visible);
                    case MainViewEvent.LoadGameClicked _ -> channel.send(new MainEvent.GameLoadRequested());
                    case MainViewEvent.ExportClicked() -> exportSelectedAsset();
                    case MainViewEvent.SearchChanged(var query) -> searchAssets(query);
                }
            });

        eventBus
            .receiverFor(AssetSelected.class)
            .consume(event -> selectAsset(event.asset()));
    }

    private void selectArchive(String archiveName) {
        try {
            archive = game.loadArchive(archiveName);
            fileList.setAssets(archive.assets());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void selectAsset(Asset asset) {
        if (getView().isPreviewVisible()) {
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

    private void exportSelectedAsset() {
        var asset = fileList.getSelectedAsset();
        if (asset == null) {
            return;
        }

        // TODO: Clean this up
        var extension = Exporter.forType(asset.type().clazz()).stream().findFirst().orElseThrow().getExtension();
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

    private void searchAssets(String query) {
        List<Asset> assets;
        if (query.isBlank()) {
            assets = archive.assets();
        } else {
            assets = archive.assets().stream()
                .filter(asset -> asset.id().fullName().contains(query))
                .toList();
        }
        fileList.setAssets(assets);
    }

    public void setGame(Game game) {
        this.game = game;
        getView().setArchives(game.archiveNames());
    }

    public void focusOnSearch() {
        getView().focusOnSearch();
    }
}
