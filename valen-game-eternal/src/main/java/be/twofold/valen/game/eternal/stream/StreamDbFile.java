package be.twofold.valen.game.eternal.stream;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.reader.streamdb.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class StreamDbFile implements Closeable {
    private static final Logger log = LoggerFactory.getLogger(StreamDbFile.class);

    private final Map<Long, StreamDbEntry> index;
    private DataSource source;

    public StreamDbFile(Path path) throws IOException {
        log.info("Loading streamdb: {}", path);
        this.source = DataSource.fromPath(path);

        var entries = StreamDb.read(source).entries();
        this.index = entries.stream()
            .collect(Collectors.toUnmodifiableMap(
                StreamDbEntry::identity,
                Function.identity()
            ));
    }


    public Optional<StreamDbEntry> get(long identity) {
        return Optional.ofNullable(index.get(identity));
    }

    public byte[] read(StreamDbEntry entry) throws IOException {
        log.debug("Reading stream: {}", String.format("%016X", entry.identity()));
        source.position(entry.offset());
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
