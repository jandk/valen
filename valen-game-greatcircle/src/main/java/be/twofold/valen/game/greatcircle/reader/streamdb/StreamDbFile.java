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

        var combineds = new ArrayList<Combined>();
        Map<Long, StreamDbEntry> entries = new HashMap<>();
        for (var i = 0; i < streamDb.identities().length; i++) {
            var identity = streamDb.identities()[i];
            var entry = streamDb.entries().get(i);
            var combined = new Combined(identity, entry.offset(), entry.length());
            combineds.add(combined);
            entries.put(identity, entry);
        }

        List<Combined> sorted = combineds.stream()
            .sorted(Comparator.comparing(Combined::offset))
            .toList();



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

    public Map<Long, StreamDbEntry> index() {
        return index;
    }

    record Combined(
        long identity,
        long offset,
        int size
    ) {
    }
}
