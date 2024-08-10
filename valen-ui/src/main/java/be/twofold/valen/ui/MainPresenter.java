package be.twofold.valen.ui;

import be.twofold.tinybcdec.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import jakarta.inject.*;
import javafx.scene.control.*;

import java.io.*;
import java.util.*;

public class MainPresenter extends AbstractPresenter<MainView> {
    private Archive<?> archive;

    @Inject
    MainPresenter(MainView view) {
        super(view);

        getView().addListener(new MainViewListener() {
            @Override
            public void onPathSelected(String path) {
                loadResources(path);
            }

            @Override
            public void onAssetSelected(Asset<?> asset) {
//                if (asset.type() == AssetType.Image) {
//                    decodeImage((Asset<?>) asset);
//                }
            }
        });
    }

    public void setArchive(Archive<?> archive) {
        this.archive = archive;
        setResources((List) archive.assets());
    }

    private void decodeImage(Asset<?> asset) {
        Texture texture;
        try {
            texture = (Texture) archive.loadAsset((Asset) asset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var decoder = switch (texture.format()) {
            case Bc1UNorm, Bc1UNormSrgb -> BlockDecoder.create(BlockFormat.BC1, PixelOrder.BGRA);
            case Bc3UNorm, Bc3UNormSrgb -> BlockDecoder.create(BlockFormat.BC3, PixelOrder.BGRA);
            case Bc4UNorm -> BlockDecoder.create(BlockFormat.BC4Unsigned, PixelOrder.BGRA);
            case Bc5UNorm -> BlockDecoder.create(BlockFormat.BC5UnsignedNormalized, PixelOrder.BGRA);
            case Bc7UNorm, Bc7UNormSrgb -> BlockDecoder.create(BlockFormat.BC7, PixelOrder.BGRA);
            default -> null;
        };

        if (decoder != null) {
            byte[] decoded = decoder.decode(texture.width(), texture.height(), texture.surfaces().getFirst().data(), 0);
            getView().setImage(decoded, texture.width(), texture.height());
        }
    }

    public void setResources(List<Asset<?>> assets) {
        Node node = buildNodeTree(assets);
        TreeItem<String> convert = convert(node);
        getView().setFileTree(convert);
    }

    private void loadResources(String path) {
        var assets = archive.assets().stream()
            .filter(r -> r.identifier().pathName().equals(path))
            .toList();

        getView().setAssets((List) assets);
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

    private Node buildNodeTree(List<Asset<?>> assets) {
        var root = new Node("root");
        for (var entry : assets) {
            var node = root;
            var path = entry.identifier().pathName();
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
