package org.redeye.valen.game.halflife2.providers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.halflife2.*;
import org.redeye.valen.game.halflife2.readers.Reader;
import org.redeye.valen.game.halflife2.readers.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public class FolderProvider implements Provider {
    private final Path root;
    private final HashMap<AssetID, Asset> assets = new HashMap<>();
    private final List<Reader> readers;
    private final Provider parent;

    public FolderProvider(Path root, Provider parent) {
        this.root = root;
        this.parent = parent;

        readers = List.of(new VtfReader());

        try (var files = Files.walk(root, 100)) {
            files.forEach(file -> {
                try {
                    if (!Files.isDirectory(file)) {
                        final String relativePath = root.relativize(file).toString().replace('\\', '/');
                        final SourceAssetID id = new SourceAssetID(root.getFileName().toString(), relativePath);
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
        return assets.values().stream().toList();
    }

    @Override
    public boolean exists(AssetID identifier) {
        return assets.containsKey(identifier);
    }

    @Override
    public Object loadAsset(AssetID identifier) throws IOException {
        final Asset asset = assets.get(identifier);
        if (identifier instanceof SourceAssetID sourceIdentifier) {
            var reader = readers.stream().filter(rdr -> rdr.canRead(asset)).findFirst().orElseThrow();
            return reader.read(this.getParent(), asset, ByteArrayDataSource.fromBuffer(loadRawAsset(sourceIdentifier)));
        }
        return null;
    }

    @Override
    public ByteBuffer loadRawAsset(AssetID identifier) throws IOException {
        return ByteBuffer.wrap(Files.readAllBytes(root.resolve(identifier.pathName())));
    }
}
