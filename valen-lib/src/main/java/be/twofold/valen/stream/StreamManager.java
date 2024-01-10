package be.twofold.valen.stream;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.packagemapspec.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class StreamManager {

    private final Path base;
    private final PackageMapSpec spec;
    private Map<Long, StreamDbFile> index;

    public StreamManager(Path base, PackageMapSpec spec) throws IOException {
        this.base = Check.notNull(base, "base must not be null");
        this.spec = Check.notNull(spec, "spec must not be null");

        loadFiles();
    }

    public boolean contains(long identity) {
        return index.containsKey(identity);
    }

    public byte[] read(long identity, int uncompressedSize) {
        var file = index.get(identity);
        Check.argument(file != null, () -> String.format("Unknown stream: 0x%016x", identity));

        return file.read(identity, uncompressedSize);
    }

    private void loadFiles() throws IOException {
        var paths = spec.files().stream()
            .filter(s -> s.endsWith(".streamdb"))
            .map(base::resolve)
            .toList();

        var index = new HashMap<Long, StreamDbFile>();
        for (var path : paths) {
            var file = new StreamDbFile(path);
            for (var entry : file.getEntries()) {
                index.putIfAbsent(entry.identity(), file);
            }
        }

        this.index = Map.copyOf(index);
    }
}
