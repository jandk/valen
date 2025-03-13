package org.redeye.valen.game.source1.collection;

import be.twofold.valen.core.game.*;
import org.redeye.valen.game.source1.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class FolderCollection implements Container<SourceAssetID, SourceAsset> {
    private final Path root;
    private final Map<SourceAssetID, SourceAsset> assets;

    public FolderCollection(Path root) throws IOException {
        this.root = root;
        try (var stream = Files.walk(root, 100)) {
            this.assets = stream
                .filter(Files::isRegularFile)
                .map(path -> mapToAsset(root, path))
                .collect(Collectors.toUnmodifiableMap(SourceAsset::id, Function.identity()));
        }
    }

    private SourceAsset mapToAsset(Path root, Path path) {
        try {
            var relativePath = root.relativize(path).toString().replace('\\', '/');
            var assetID = new SourceAssetID(relativePath);
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
    public ByteBuffer read(SourceAssetID key, Integer size) throws IOException {
        return ByteBuffer.wrap(Files.readAllBytes(root.resolve(key.fullName())));
    }

    @Override
    public void close() {
    }
}
