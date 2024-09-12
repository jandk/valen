package be.twofold.valen.ui.window;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.util.*;
import jakarta.inject.*;
import javafx.scene.control.*;

import java.io.*;
import java.util.*;

public class MainPresenter extends AbstractPresenter<MainView> {
    private Archive archive;
    private Asset lastAsset;

    @Inject
    MainPresenter(MainView view) {
        super(view);

        getView().addListener(new MainViewListener() {
            @Override
            public void onPathSelected(String path) {
                loadResources(path);
            }

            @Override
            public void onAssetSelected(Asset asset) {
                if (getView().isPreviewVisible()) {
                    try {
                        Object assetData;
                        if (asset.type() == AssetType.Binary) {
                            assetData = archive.loadRawAsset(asset.id());
                        } else {
                            assetData = archive.loadAsset(asset.id());
                        }
                        getView().setupPreview(asset, assetData);
                    } catch (
                        IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    lastAsset = asset;
                }
            }

            @Override
            public void onPreviewVisibleChanged(boolean visible) {
                if (visible && lastAsset != null) {
                    onAssetSelected(lastAsset);
                }
            }
        });
    }

    public void setArchive(Archive archive) {
        this.archive = archive;
        setResources(archive.assets());
    }

    public void setResources(List<Asset> assets) {
        Node node = buildNodeTree(assets);
        TreeItem<String> convert = convert(node);
        getView().setFileTree(convert);
    }

    private void loadResources(String path) {
        var assets = archive.assets().stream()
            .filter(r -> r.id().pathName().equals(path))
            .toList();

        getView().setAssets(assets);
    }

    private TreeItem<String> convert(Node node) {
        var children = node.children.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(NaturalOrderComparator.instance()))
            .map(Map.Entry::getValue)
            .toList();

        var item = new TreeItem<>(node.name);
        for (var child : children) {
            item.getChildren().add(convert(child));
        }
        return item;
    }

    private Node buildNodeTree(List<Asset> assets) {
        var root = new Node("root");
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

    private static final class Node {
        private final String name;
        private final Map<String, Node> children = new HashMap<>();

        public Node(String name) {
            this.name = name;
        }

        public Node get(String name) {
            return children.computeIfAbsent(name, Node::new);
        }
    }
}
