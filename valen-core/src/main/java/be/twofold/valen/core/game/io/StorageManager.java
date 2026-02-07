package be.twofold.valen.core.game.io;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public class StorageManager implements Closeable {
    private final Map<FileId, BinarySource> sources;
    private final Set<FileId> sharedIds;

    public StorageManager(Map<FileId, BinarySource> sources, Set<FileId> sharedIds) {
        this.sources = Map.copyOf(sources);
        this.sharedIds = Set.copyOf(sharedIds);
    }

    public final Bytes open(Location location) throws IOException {
        if (location instanceof Location.FileSlice fileSlice) {
            return sources.get(fileSlice.fileId())
                .position(fileSlice.offset())
                .readBytes(fileSlice.size());
        }
        if (location instanceof Location.Compressed compressed) {
            Bytes compressedData = open(compressed.base());
            return Decompressors.get(compressed.type())
                .decompress(compressedData, compressed.uncompressedSize());
        }
        return openCustom(location);
    }

    protected Bytes openCustom(Location location) throws IOException {
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
