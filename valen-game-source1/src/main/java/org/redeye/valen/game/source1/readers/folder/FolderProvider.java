package org.redeye.valen.game.source1.readers.folder;

import be.twofold.valen.core.game.*;
import org.redeye.valen.game.source1.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class FolderProvider implements Container<SourceAssetID, SourceAsset> {
    private final Path root;
    private final String name;
    private final Map<SourceAssetID, SourceAsset> assets;

    public FolderProvider(Path root) throws IOException {
        this.root = root;
        this.name = root.getFileName().toString();
        try (var stream = Files.walk(root, 100)) {
            this.assets = stream
                .filter(Files::isRegularFile)
                .map(path -> {
                    return mapToAsset(root, path);
                })
                .collect(Collectors.toUnmodifiableMap(SourceAsset::id, Function.identity()));
        }
    }

    private SourceAsset mapToAsset(Path root, Path path) {
        try {
            var relativePath = root.relativize(path).toString().replace('\\', '/');
            var assetID = new SourceAssetID(relativePath, name);
            return new SourceAsset.File(assetID, Math.toIntExact(Files.size(path)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Optional<SourceAsset> get(SourceAssetID key) {
        return Optional.ofNullable(assets.get(key));
    }

    @Override
    public Stream<SourceAsset> getAll() {
        return assets.values().stream();
    }

    @Override
    public ByteBuffer read(SourceAssetID key, int uncompressedSize) throws IOException {
        return ByteBuffer.wrap(Files.readAllBytes(root.resolve(key.fullName())));
    }

    @Override
    public void close() {
    }
}
