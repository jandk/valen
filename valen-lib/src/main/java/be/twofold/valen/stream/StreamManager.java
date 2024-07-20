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
    private List<StreamDbFile> files;

    @Inject
    StreamManager(DecompressorService decompressorService) {
        this.decompressorService = decompressorService;
    }

    public void load(Path base, PackageMapSpec spec) throws IOException {
        this.base = Check.notNull(base, "base must not be null");
        this.spec = Check.notNull(spec, "spec must not be null");

        loadFiles();
    }

    public boolean exists(long identity) {
        return files.stream()
            .anyMatch(f -> f.get(identity).isPresent());
    }

    public byte[] read(long identity, int uncompressedSize) throws IOException {
        var compressed = read(identity);
        if (compressed.length == uncompressedSize) {
            return compressed;
        }

        var decompressed = new byte[uncompressedSize];
        decompressorService.decompress(ByteBuffer.wrap(compressed), ByteBuffer.wrap(decompressed), CompressionType.Kraken);
        return decompressed;
    }

    private byte[] read(long identity) throws IOException {
        for (var file : files) {
            var entry = file.get(identity);
            if (entry.isPresent()) {
                return file.read(entry.get());
            }
        }
        throw new IOException(String.format("Unknown stream: 0x%016x", identity));
    }

    private void loadFiles() throws IOException {
        var paths = spec.files().stream()
            .filter(s -> s.endsWith(".streamdb"))
            .map(base::resolve)
            .toList();

        var files = new ArrayList<StreamDbFile>();
        for (var path : paths) {
            files.add(new StreamDbFile(path));
        }
        this.files = List.copyOf(files);
    }
}
