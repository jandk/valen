package be.twofold.valen.game.colossus;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.colossus.reader.*;
import be.twofold.valen.game.colossus.reader.image.*;
import be.twofold.valen.game.colossus.resource.*;
import be.twofold.valen.game.colossus.texdb.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public class ColossusArchive implements Archive {
    private final ResourcesCollection resources;
    private final TexDbFile texDb;
    private final List<ResourceReader<?>> readers;

    public ColossusArchive(
        ResourcesCollection resources,
        TexDbFile texDb
    ) {
        this.resources = resources;
        this.texDb = texDb;
        this.readers = List.of(
            new ImageReader(this)
        );
    }

    @Override
    public List<Asset> assets() {
        return resources.getEntries().stream()
            .map(this::toAsset)
            .toList();
    }

    @Override
    public boolean exists(AssetID id) {
        return resources.get((ResourceKey) id).isPresent();
    }

    @Override
    public Object loadAsset(AssetID id) throws IOException {
        var resource = resources.get((ResourceKey) id)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + id));

        var reader = readers.stream()
            .filter(r -> r.canRead(resource.key()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No reader found for resource: " + resource));

        var buffer = resources.read(resource);
        try (var source = ByteArrayDataSource.fromBuffer(buffer)) {
            return reader.read(source, toAsset(resource));
        }
    }

    @Override
    public ByteBuffer loadRawAsset(AssetID id) throws IOException {
        var resource = resources.get((ResourceKey) id)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + id));

        return resources.read(resource);
    }

    private Asset toAsset(Resource r) {
        return new Asset(
            r.key(),
            mapAssetType(r.key().type()),
            r.compressedSize(),
            Map.of("hash", r.hash())
        );
    }

    private AssetType mapAssetType(ResourceType type) {
        if (type == ResourceType.image) {
            return AssetType.Image;
        }
        return AssetType.Binary;
    }

    public boolean containsStream(long hash) {
        return texDb.get(hash).isPresent();
    }

    public ByteBuffer readStream(long hash, int compressedSize, int uncompressedSize) throws IOException {
        var entry = texDb.get(hash)
            .orElseThrow(() -> new IllegalArgumentException("Stream not found: " + hash));

        if (entry.size() != 1) {
            throw new UnsupportedOperationException("Multiple entries for stream: " + hash);
        }

        byte[] compressed = texDb.read(entry.getFirst(), compressedSize);
        if (compressed.length == uncompressedSize) {
            return ByteBuffer.wrap(compressed);
        }

        return Decompressor
            .forType(CompressionType.Kraken)
            .decompress(ByteBuffer.wrap(compressed), uncompressedSize);
    }
}
