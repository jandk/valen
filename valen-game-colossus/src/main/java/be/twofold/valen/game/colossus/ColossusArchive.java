package be.twofold.valen.game.colossus;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.colossus.reader.*;
import be.twofold.valen.game.colossus.reader.image.*;
import be.twofold.valen.game.colossus.reader.texdb.*;
import be.twofold.valen.game.colossus.resource.*;
import be.twofold.valen.game.colossus.texdb.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public class ColossusArchive implements Archive {
    private final List<ResourcesFile> resources;
    private final List<TexDbFile> texDbs;
    private final List<ResourceReader<?>> readers;

    public ColossusArchive(
        List<ResourcesFile> resources,
        List<TexDbFile> texDbs
    ) {
        this.resources = List.copyOf(resources);
        this.texDbs = List.copyOf(texDbs);
        this.readers = List.of(
            new ImageReader(this)
        );
    }

    @Override
    public List<Asset> assets() {
        return resources.stream()
            .flatMap(r -> r.getResources().stream())
            .map(this::toAsset)
            .distinct()
            .toList();
    }

    @Override
    public boolean exists(AssetID id) {
        return findResource((ResourceKey) id).isPresent();
    }

    @Override
    public Object loadAsset(AssetID id) throws IOException {
        var resource = findResource((ResourceKey) id)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + id));

        var reader = readers.stream()
            .filter(r -> r.canRead(resource.key()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No reader found for resource: " + resource));

        var buffer = read(resource);
        try (var source = ByteArrayDataSource.fromBuffer(buffer)) {
            return reader.read(source, toAsset(resource));
        }
    }

    @Override
    public ByteBuffer loadRawAsset(AssetID id) throws IOException {
        var resource = findResource((ResourceKey) id)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + id));

        return read(resource);
    }

    public boolean containsStream(long hash) {
        return texDbs.stream().anyMatch(f -> f.get(hash).isPresent());
    }

    public ByteBuffer readStream(long hash, int compressedSize, int uncompressedSize) throws IOException {
        for (TexDbFile texDb : texDbs) {
            var entry = texDb.get(hash);
            if (entry.isEmpty()) {
                continue;
            }

            // TODO: Check if getFirst is actually the correct thing to do?
            //       It seems like the hashes resolve to the same content, but
            //       it's not clear if that's always the case.
            var compressed = texDb.read(entry.get().getFirst(), compressedSize);
            if (compressed.length == uncompressedSize) {
                return ByteBuffer.wrap(compressed);
            }

            return Decompressor
                .forType(CompressionType.Kraken)
                .decompress(ByteBuffer.wrap(compressed), uncompressedSize);
        }
        throw new IOException("Unknown stream: " + hash);
    }

    private Optional<Resource> findResource(ResourceKey id) {
        return resources.stream()
            .flatMap(f -> f.get(id).stream())
            .findFirst();
    }

    private Optional<List<TexDbEntry>> findTexDbEntry(long hash) {
        return texDbs.stream()
            .flatMap(f -> f.get(hash).stream())
            .findFirst();
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

    private ByteBuffer read(Resource resource) throws IOException {
        for (var file : resources) {
            var entry = file.get(resource.key());
            if (entry.isEmpty()) {
                continue;
            }
            var compressed = file.read(entry.get());
            return Decompressor
                .forType(resource.compression())
                .decompress(ByteBuffer.wrap(compressed), resource.uncompressedSize());
        }
        throw new IOException("Unknown resource: " + resource.key());
    }
}
