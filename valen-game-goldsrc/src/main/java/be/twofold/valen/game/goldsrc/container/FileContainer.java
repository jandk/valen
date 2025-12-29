package be.twofold.valen.game.goldsrc.container;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.goldsrc.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class FileContainer implements Container<GoldSrcAssetID, GoldSrcAsset> {
    private final Path root;
    private final Map<GoldSrcAssetID, GoldSrcAsset> index;

    public FileContainer(Path root) {
        this.root = root;
        try (var stream = Files.walk(root, 100)) {
            this.index = stream
                .filter(Files::isRegularFile)
                .map(path -> mapToAsset(root, path))
                .collect(Collectors.toUnmodifiableMap(GoldSrcAsset::id, Function.identity()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private GoldSrcAsset mapToAsset(Path root, Path path) {
        try {
            var relativePath = root.relativize(path).toString().replace('\\', '/');
            var assetID = new GoldSrcAssetID(relativePath);
            return new GoldSrcAsset.File(assetID, Math.toIntExact(Files.size(path)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Optional<GoldSrcAsset> get(GoldSrcAssetID key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<GoldSrcAsset> getAll() {
        return index.values().stream();
    }

    @Override
    public Bytes read(GoldSrcAssetID key, Integer size) throws IOException {
        return Bytes.wrap(Files.readAllBytes(root.resolve(key.fullName())));
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }
}
