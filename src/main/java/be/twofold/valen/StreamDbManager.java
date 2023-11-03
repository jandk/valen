package be.twofold.valen;

import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.streamdb.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public final class StreamDbManager implements StreamLoader {
    private final FileManager fileManager;
    private final Map<Long, String> hashToPath = new HashMap<>();
    private final Map<Long, StreamDbEntry> hashToEntry = new HashMap<>();

    StreamDbManager(FileManager fileManager) {
        this.fileManager = Objects.requireNonNull(fileManager);
        initialize();
    }

    private void initialize() {
        List<String> paths = fileManager.getSpec().files().stream()
            .filter(file -> file.endsWith(".streamdb"))
            .toList();

        /*
         * We can actually get away with loading all streamdb files into memory.
         * The order in packagemapspec.json is consistent across all maps, so
         * we can just load them in order and then use the first one that has
         * the entry we're looking for.
         */
        for (String path : paths) {
            System.out.println("Loading streamdb: " + path);
            SeekableByteChannel channel = fileManager.open(path);

            StreamDb db = StreamDbReader.read(channel);
            for (StreamDbEntry entry : db.entries()) {
                hashToPath.putIfAbsent(entry.identity(), path);
                hashToEntry.putIfAbsent(entry.identity(), entry);
            }
        }
    }

    @Override
    public Optional<byte[]> load(long identity, int size) {
        String path = hashToPath.get(identity);
        if (path == null) {
            return Optional.empty();
        }

        StreamDbEntry entry = hashToEntry.get(identity);
        SeekableByteChannel channel = fileManager.open(path);
        try {
            channel.position(entry.offset());
            byte[] compressed = IOUtils.readBytes(channel, entry.length());
            byte[] decompressed = OodleDecompressor.decompress(compressed, size);
            return Optional.of(decompressed);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to read stream: 0x%016x", entry.identity()), e);
        }
    }

    @Override
    public boolean exists(long identity) {
        return hashToPath.containsKey(identity);
    }
}
