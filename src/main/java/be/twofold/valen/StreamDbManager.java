package be.twofold.valen;

import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.reader.streamdb.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public final class StreamDbManager implements StreamLoader, AutoCloseable {
    private final Map<String, SeekableByteChannel> pathToChannel;
    private final Map<Long, String> hashToPath;
    private final Map<Long, StreamDbEntry> hashToEntry;

    private StreamDbManager(
        Map<String, SeekableByteChannel> pathToChannel,
        Map<Long, String> hashToPath,
        Map<Long, StreamDbEntry> hashToEntry
    ) {
        this.pathToChannel = Map.copyOf(pathToChannel);
        this.hashToPath = Map.copyOf(hashToPath);
        this.hashToEntry = Map.copyOf(hashToEntry);
    }

    public static StreamDbManager load(Path base, PackageMapSpec spec) throws IOException {
        List<String> paths = spec.files().stream()
            .filter(file -> file.endsWith(".streamdb"))
            .toList();

        Map<String, SeekableByteChannel> pathToChannel = new HashMap<>();
        Map<Long, String> hashToPath = new HashMap<>();
        Map<Long, StreamDbEntry> hashToEntry = new HashMap<>();

        /*
         * We can actually get away with loading all streamdb files into memory.
         * The order in packagemapspec.json is consistent across all maps, so
         * we can just load them in order and then use the first one that has
         * the entry we're looking for.
         */
        for (String path : paths) {
            System.out.println("Loading streamdb: " + path);
            Path fullPath = base.resolve(path);
            SeekableByteChannel channel = Files.newByteChannel(fullPath);
            pathToChannel.put(path, channel);

            StreamDb db = new StreamDbReader(channel).read();
            for (StreamDbEntry entry : db.entries()) {
                hashToPath.putIfAbsent(entry.identity(), path);
                hashToEntry.putIfAbsent(entry.identity(), entry);
            }
        }

        return new StreamDbManager(pathToChannel, hashToPath, hashToEntry);
    }

    @Override
    public Optional<byte[]> load(long identity, int size) {
        String path = hashToPath.get(identity);
        if (path == null) {
            return Optional.empty();
        }

        StreamDbEntry entry = hashToEntry.get(identity);
        SeekableByteChannel channel = pathToChannel.get(path);
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

    @Override
    public void close() throws IOException {
        for (SeekableByteChannel channel : pathToChannel.values()) {
            channel.close();
        }
    }
}
