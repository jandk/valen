package org.redeye.valen.game.source1.providers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.source1.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FolderProvider implements Provider {
    private final Path root;
    private final HashMap<AssetID, Asset> assets = new HashMap<>();
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
                        assets.put(id, new Asset(id, id.identifyAssetType(), (int) Files.size(file), Map.of()));
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
        return this.root.getFileName().toString();
    }

    @Override
    public Provider getParent() {
        return this.parent == null ? this : this.parent.getParent();
    }

    @Override
    public List<Asset> assets() {
        return List.copyOf(assets.values());
    }

    @Override
    public boolean exists(AssetID identifier) {
        return assets.containsKey(identifier);
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        final Asset asset = assets.get(identifier);
        if (identifier instanceof SourceAssetID sourceIdentifier) {
            var bytes = Files.readAllBytes(root.resolve(identifier.fullName()));

            if (clazz == byte[].class) {
                return (T) bytes;
            }

            var reader = getReaders().stream()
                .filter(r -> r.canRead(asset))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No reader found for resource: " + asset.toString()));

            try (var source = DataSource.fromArray(bytes)) {
                return clazz.cast(reader.read(getParent(), asset, source));
            }
        }
        return null;
    }
}
