package be.twofold.valen.ui.window;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.event.*;
import be.twofold.valen.ui.util.*;
import jakarta.inject.*;
import javafx.application.*;

import java.io.*;
import java.util.*;

public final class MainPresenter extends AbstractPresenter<MainView> {
    private Game game;
    private Archive archive;
    private Asset lastAsset;

    @Inject
    MainPresenter(MainView view, EventBus eventBus) {
        super(view);

        eventBus
            .receiverFor(MainViewEvent.class)
            .consume(event -> {
                switch (event) {
                    case MainViewEvent.ArchiveSelected(var name) -> selectArchive(name);
                    case MainViewEvent.PathSelected(var name) -> selectPath(name);
                    case MainViewEvent.AssetSelected(var name) -> selectAsset(name);
                    case MainViewEvent.PreviewVisibilityChanged(var visible) -> showPreview(visible);
                }
            });
    }

    private void selectArchive(String archiveName) {
        try {
            archive = game.loadArchive(archiveName);
            Platform.runLater(() -> {
                getView().setFileTree(buildNodeTree(archive.assets()));
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void selectPath(String path) {
        var assets = archive.assets().stream()
            .filter(r -> r.id().pathName().equals(path))
            .toList();

        Platform.runLater(() -> {
            getView().setFilteredAssets(assets);
        });
    }

    private void selectAsset(Asset asset) {
        if (getView().isPreviewVisible()) {
            try {
                Object assetData;
                if (asset.type() == AssetType.Binary) {
                    assetData = archive.loadRawAsset(asset.id());
                } else {
                    assetData = archive.loadAsset(asset.id());
                }
                Platform.runLater(() -> {
                    getView().setupPreview(asset, assetData);
                });
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

    public void setGame(Game game) {
        this.game = game;
        getView().setArchives(game.archiveNames());
    }

    public void focusOnSearch() {
        getView().focusOnSearch();
    }

    private PathNode<String> buildNodeTree(List<Asset> assets) {
        var root = new PathNode<>("root");
        for (var asset : assets) {
            var node = root;
            var path = asset.id().pathName();
            if (!path.isEmpty()) {
                for (var s : path.split("/")) {
                    node = node.get(s);
                }
            }
        }
        return root;
    }
}
