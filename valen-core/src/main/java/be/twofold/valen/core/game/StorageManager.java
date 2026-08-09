package be.twofold.valen.core.game;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class StorageManager implements Closeable {
    private final Map<Path, BinarySource> sources;
    private final Set<Path> sharedIds;
    private final Decompressors decompressors;

    public StorageManager(Map<Path, BinarySource> sources, Set<Path> shared, Decompressors decompressors) {
        this.sources = Map.copyOf(sources);
        this.sharedIds = Set.copyOf(shared);
        this.decompressors = Objects.requireNonNull(decompressors, "decompressors");
    }

    public final Bytes open(Location location) throws IOException {
        return switch (location) {
            case Location.FullFile fullFile -> Bytes.wrap(Files.readAllBytes(fullFile.path()));
            case Location.FileSlice fileSlice -> sources.get(fileSlice.path())
                .position(fileSlice.offset())
                .readBytes(fileSlice.size());
            case Location.InMemory inMemory -> inMemory.data();
            case Location.Compressed compressed -> {
                Bytes compressedData = open(compressed.base());
                yield decompress(compressed.type(), compressedData, compressed.size());
            }
            case Location.Custom custom -> openCustom(custom);
        };
    }

    public final Bytes decompress(CompressionType type, Bytes data, int uncompressedSize) {
        return decompressors.get(type).decompress(data, uncompressedSize);
    }

    protected Bytes openCustom(Location.Custom custom) throws IOException {
        throw new UnsupportedOperationException("Unsupported location: " + custom);
    }

    @Override
    public final void close() throws IOException {
        for (var entry : sources.entrySet()) {
            // Only close if it's not shared
            if (!sharedIds.contains(entry.getKey())) {
                entry.getValue().close();
            }
        }
    }
}
