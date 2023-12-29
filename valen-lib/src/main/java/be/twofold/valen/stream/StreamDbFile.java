package be.twofold.valen.stream;

import be.twofold.valen.core.util.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.streamdb.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class StreamDbFile implements AutoCloseable {
    private final Map<Long, StreamDbEntry> entries;
    private SeekableByteChannel channel;

    public StreamDbFile(Path path) throws IOException {
        System.out.println("Loading streamdb: " + path);

        this.channel = Files.newByteChannel(path, StandardOpenOption.READ);
        this.entries = StreamDb.read(channel).entries().stream()
            .collect(Collectors.toUnmodifiableMap(
                StreamDbEntry::identity,
                Function.identity()
            ));
    }

    public Collection<StreamDbEntry> getEntries() {
        return entries.values();
    }

    public byte[] read(long identity, int uncompressedSize) {
        var entry = entries.get(identity);

        try {
            channel.position(entry.offset());
            var compressed = IOUtils.readBytes(channel, entry.length());
            return OozDecompressor.decompress(compressed, uncompressedSize);
        } catch (IOException e) {
            System.out.println("Error reading stream: " + identity);
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            channel.close();
            channel = null;
        }
    }
}
