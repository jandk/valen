package be.twofold.valen.stream;

import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.streamdb.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class StreamDbFile implements AutoCloseable {
    private final Map<Long, StreamDbEntry> entries;
    private DataSource source;

    public StreamDbFile(Path path) throws IOException {
        System.out.println("Loading streamdb: " + path);

        var channel = Files.newByteChannel(path, StandardOpenOption.READ);
        this.source = new ChannelDataSource(channel);
        this.entries = StreamDb.read(source).entries().stream()
            .collect(Collectors.toUnmodifiableMap(
                StreamDbEntry::identity,
                Function.identity()
            ));
    }

    public Collection<StreamDbEntry> getEntries() {
        return entries.values();
    }

    public byte[] read(long identity) throws IOException {
        var entry = entries.get(identity);
        source.seek(entry.offset());
        return source.readBytes(entry.length());
    }

    @Override
    public void close() throws IOException {
        if (source != null) {
            source.close();
            source = null;
        }
    }
}
