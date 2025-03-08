package org.redeye.valen.game.source1.providers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.source1.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class FolderProvider implements Provider {
    private final Path root;
    private final Map<AssetID, SourceAsset> assets = new HashMap<>();
    private final Provider parent;

    public FolderProvider(Path root, Provider parent) {
        this.root = root;
        this.parent = parent;

        try (var files = Files.walk(root, 100)) {
            files.forEach(file -> {
                try {
                    if (!Files.isDirectory(file)) {
                        String relativePath = root.relativize(file).toString().replace('\\', '/');
                        SourceAssetID id = new SourceAssetID(root.getFileName().toString(), relativePath);
                        assets.put(id, new SourceAsset(id, id.identifyAssetType(), Math.toIntExact(Files.size(file)), Map.of()));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return root.getFileName().toString();
    }

    @Override
    public Provider getParent() {
        return parent == null ? this : parent.getParent();
    }

    @Override
    public Stream<? extends Asset> assets() {
        return assets.values().stream();
    }

    @Override
    public Optional<? extends Asset> getAsset(AssetID identifier) {
        return Optional.ofNullable(assets.get(identifier));
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        var asset = assets.get(identifier);
        var bytes = Files.readAllBytes(root.resolve(identifier.fullName()));

        try (var source = DataSource.fromArray(bytes)) {
            return readers.read(asset, source, clazz);
        }
    }
}
