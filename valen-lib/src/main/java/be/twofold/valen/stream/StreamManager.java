package be.twofold.valen.stream;

import be.twofold.valen.compression.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

@Singleton
public final class StreamManager {
    private final DecompressorService decompressorService;

    private Path base;
    private PackageMapSpec spec;
    private Map<Long, StreamDbFile> index;

    @Inject
    public StreamManager(DecompressorService decompressorService) {
        this.decompressorService = decompressorService;
    }

    public void load(Path base, PackageMapSpec spec) throws IOException {
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

        try {
            var compressed = file.read(identity);
            if (compressed.length == uncompressedSize) {
                return compressed;
            }

            var decompressed = new byte[uncompressedSize];
            decompressorService.decompress(ByteBuffer.wrap(compressed), ByteBuffer.wrap(decompressed), CompressionType.Kraken);
            return decompressed;
        } catch (IOException e) {
            System.out.println("Error reading stream: " + identity);
            throw new UncheckedIOException(e);
        }
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
