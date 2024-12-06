package be.twofold.valen.game.greatcircle.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class StreamDbFile implements Closeable {
    private final Map<Long, StreamDbEntry> index;
    private DataSource source;

    public StreamDbFile(Path path) throws IOException {
        System.out.println("Loading streamdb: " + path);
        this.source = DataSource.fromPath(path);

        var streamDb = StreamDb.read(source);

        Map<Long, StreamDbEntry> entries = new HashMap<>();
        for (var i = 0; i < streamDb.identities().length; i++) {
            var identity = streamDb.identities()[i];
            var entry = streamDb.entries().get(i);
            entries.put(identity, entry);
        }
        this.index = Map.copyOf(entries);
    }


    public Optional<StreamDbEntry> get(long identity) {
        return Optional.ofNullable(index.get(identity));
    }

    public byte[] read(StreamDbEntry entry) throws IOException {
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
