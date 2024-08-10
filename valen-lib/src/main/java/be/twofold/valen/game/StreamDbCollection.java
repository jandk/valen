package be.twofold.valen.game;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.stream.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public final class StreamDbCollection {
    private final List<StreamDbFile> files;

    private StreamDbCollection(List<StreamDbFile> files) {
        this.files = List.copyOf(files);
    }

    public static StreamDbCollection load(Path base, PackageMapSpec spec) throws IOException {
        var paths = spec.files().stream()
            .filter(s -> s.endsWith(".streamdb"))
            .map(base::resolve)
            .toList();

        var files = new ArrayList<StreamDbFile>();
        for (var path : paths) {
            files.add(new StreamDbFile(path));
        }
        return new StreamDbCollection(files);
    }

    public boolean exists(long identity) {
        return files.stream()
            .anyMatch(f -> f.get(identity).isPresent());
    }

    public ByteBuffer read(long identity, int uncompressedSize) throws IOException {
        for (var file : files) {
            var entry = file.get(identity);
            if (entry.isEmpty()) {
                continue;
            }

            var compressed = file.read(entry.get());
            return Decompressor
                .forType(CompressionType.Kraken)
                .decompress(ByteBuffer.wrap(compressed), uncompressedSize);
        }
        throw new IOException(String.format("Unknown stream: 0x%016x", identity));
    }
}
