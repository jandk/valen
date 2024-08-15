package be.twofold.valen.game.colossus.texdb;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.colossus.reader.texdb.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class TexDbFile implements AutoCloseable {
    private final Map<Long, List<TexDbEntry>> entries;
    private DataSource source;

    public TexDbFile(Path path) throws IOException {
        System.out.println("Loading texdb: " + path);
        this.source = new ChannelDataSource(Files.newByteChannel(path, StandardOpenOption.READ));

        var texDb = TexDb.read(source);
        this.entries = Map.copyOf(texDb.entries().stream()
            .collect(Collectors.groupingBy(TexDbEntry::hash, Collectors.toUnmodifiableList())));
    }

    public Optional<List<TexDbEntry>> get(long hash) {
        return Optional.ofNullable(entries.get(hash));
    }

    public byte[] read(TexDbEntry entry, int size) throws IOException {
        source.seek(entry.offset());
        return source.readBytes(size);
    }

    @Override
    public void close() throws IOException {
        if (source != null) {
            source.close();
            source = null;
        }
    }
}
