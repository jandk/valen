package be.twofold.valen.stream;

import be.twofold.valen.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.streamdb.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public final class StreamManager {

    private final FileManager fileManager;
    private final Map<Long, String> hashToPath = new HashMap<>();
    private final Map<Long, StreamDbEntry> hashToEntry = new HashMap<>();

    public StreamManager(FileManager fileManager) throws IOException {
        this.fileManager = Check.notNull(fileManager);
        initialize();
    }

    private void initialize() throws IOException {
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

            StreamDb db = StreamDb.read(channel);
            for (StreamDbEntry entry : db.entries()) {
                hashToPath.putIfAbsent(entry.identity(), path);
                hashToEntry.putIfAbsent(entry.identity(), entry);
            }
        }
    }

    public byte[] load(long identity, int size) {
        String path = hashToPath.get(identity);
        Check.argument(path != null, () -> String.format("Unknown stream: 0x%016x", identity));

        StreamDbEntry entry = hashToEntry.get(identity);
        SeekableByteChannel channel = fileManager.open(path);
        try {
            channel.position(entry.offset());
            byte[] compressed = IOUtils.readBytes(channel, entry.length());
            return OodleDecompressor.decompress(compressed, size);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to read stream: 0x%016x", entry.identity()), e);
        }
    }

    public boolean exists(long identity) {
        return hashToPath.containsKey(identity);
    }

}
