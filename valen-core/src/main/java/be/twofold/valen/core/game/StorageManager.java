package be.twofold.valen.core.game;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public class StorageManager implements Closeable {
    private final Map<FileId, BinarySource> sources;
    private final Decompressor oodle;

    public StorageManager(Map<FileId, BinarySource> sources, Decompressor oodle) {
        this.sources = Map.copyOf(sources);
        this.oodle = oodle;
    }

    public final Bytes open(StorageLocation location) throws IOException {
        if (location instanceof StorageLocation.FileSlice fileSlice) {
            BinarySource source = sources.get(fileSlice.fileId());
            return source.position(fileSlice.offset()).readBytes(fileSlice.size());
        }
        if (location instanceof StorageLocation.Compressed compressed) {
            Bytes compressedBytes = open(compressed.base());
            return switch (compressed.compressionType()) {
                case "oodle" -> oodle.decompress(compressedBytes, compressed.uncompressedSize());
                default -> throw new UnsupportedOperationException(compressed.compressionType());
            };
        }
        return openCustom(location);
    }

    protected Bytes openCustom(StorageLocation location) throws IOException {
        throw new UnsupportedOperationException("Custom locations are not supported for this game.");
    }

    protected void onClose() throws IOException {
    }

    @Override
    public final void close() throws IOException {
        for (BinarySource source : sources.values()) {
            source.close();
        }
        onClose();
    }
}
