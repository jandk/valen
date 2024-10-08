package be.twofold.valen.game.eternal;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.game.eternal.reader.packagemapspec.*;
import be.twofold.valen.game.eternal.stream.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

final class StreamDbCollection {
    private final List<StreamDbFile> files;

    private StreamDbCollection(List<StreamDbFile> files) {
        this.files = List.copyOf(files);
    }

    static StreamDbCollection load(Path base, PackageMapSpec spec) throws IOException {
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

    boolean exists(long identity) {
        return files.stream()
            .anyMatch(f -> f.get(identity).isPresent());
    }

    ByteBuffer read(long identity, int uncompressedSize) throws IOException {
        for (var file : files) {
            var entry = file.get(identity);
            if (entry.isEmpty()) {
                continue;
            }

            var compressed = file.read(entry.get());
            if (compressed.length == uncompressedSize) {
                return ByteBuffer.wrap(compressed);
            }

            return Compression.Oodle
                .decompress(ByteBuffer.wrap(compressed), uncompressedSize);
        }
        throw new IOException(String.format("Unknown stream: 0x%016x", identity));
    }
}
