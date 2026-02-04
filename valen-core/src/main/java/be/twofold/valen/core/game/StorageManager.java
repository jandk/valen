package be.twofold.valen.core.game;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public class StorageManager implements Closeable {
    private final Map<FileId, BinarySource> sources;
    private final Set<FileId> sharedIds;
    private final Decompressor oodle;

    public StorageManager(Map<FileId, BinarySource> sources, Set<FileId> sharedIds, Decompressor oodle) {
        this.sources = Map.copyOf(sources);
        this.sharedIds = Set.copyOf(sharedIds);
        this.oodle = oodle;
    }

    public final Bytes open(StorageLocation location) throws IOException {
        if (location instanceof StorageLocation.FileSlice fileSlice) {
            return sources.get(fileSlice.fileId())
                .position(fileSlice.offset())
                .readBytes(fileSlice.size());
        }
        if (location instanceof StorageLocation.Compressed compressed) {
            var compressedBytes = open(compressed.base());
            return switch (compressed.compressionType()) {
                case "oodle" -> oodle.decompress(compressedBytes, compressed.uncompressedSize());
                default -> throw new UnsupportedOperationException(compressed.compressionType());
            };
        }
        return openCustom(location);
    }

    protected Bytes openCustom(StorageLocation location) throws IOException {
        throw new UnsupportedOperationException("Unsupported location: " + location);
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
