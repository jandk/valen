package be.twofold.valen.ui.window;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.event.*;
import be.twofold.valen.ui.util.*;
import jakarta.inject.*;
import javafx.application.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public final class MainPresenter extends AbstractPresenter<MainView> {
    private final SendChannel<MainEvent> channel;

    private Game game;
    private Archive archive;
    private Asset lastAsset;

    @Inject
    MainPresenter(MainView view, EventBus eventBus) {
        super(view);

        this.channel = eventBus.senderFor(MainEvent.class);
        eventBus
            .receiverFor(MainViewEvent.class)
            .consume(event -> {
                switch (event) {
                    case MainViewEvent.ArchiveSelected(var name) -> selectArchive(name);
                    case MainViewEvent.PathSelected(var name) -> selectPath(name);
                    case MainViewEvent.AssetSelected(var name) -> selectAsset(name);
                    case MainViewEvent.PreviewVisibilityChanged(var visible) -> showPreview(visible);
                    case MainViewEvent.LoadGameClicked _ -> channel.send(new MainEvent.GameLoadRequested());
                    case MainViewEvent.ExportClicked(var asset) -> exportSelectedAsset(asset);
                }
            });
    }

    private void selectArchive(String archiveName) {
        try {
            archive = game.loadArchive(archiveName);
            Platform.runLater(() -> getView().setFileTree(buildNodeTree(archive.assets())));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void selectPath(String path) {
        var assets = archive.assets().stream()
            .filter(r -> r.id().pathName().equals(path))
            .toList();

        Platform.runLater(() -> getView().setFilteredAssets(assets));
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

    private void exportSelectedAsset(Asset asset) {
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

    public void setGame(Game game) {
        this.game = game;
        getView().setArchives(game.archiveNames());
    }

    public void focusOnSearch() {
        getView().focusOnSearch();
    }

    private PathNode<String> buildNodeTree(List<Asset> assets) {
        var root = new PathNode<>("root", true);
        for (var asset : assets) {
            var node = root;
            var path = asset.id().pathName();
            if (!path.isEmpty()) {
                var split = path.split("/");
                for (var i = 0; i < split.length; i++) {
                    var hasFiles = i == split.length - 1;
                    node = node.get(split[i], hasFiles);
                }
            }
        }
        return root;
    }
}
