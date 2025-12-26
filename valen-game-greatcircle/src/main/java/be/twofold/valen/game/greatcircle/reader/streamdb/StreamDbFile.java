package be.twofold.valen.game.greatcircle.reader.streamdb;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class StreamDbFile implements Container<Long, StreamDbEntry> {
    private static final Logger log = LoggerFactory.getLogger(StreamDbFile.class);

    private final Map<Long, StreamDbEntry> index;
    private final Decompressor decompressor;
    private BinarySource source;

    public StreamDbFile(Path path, Decompressor decompressor) throws IOException {
        log.info("Loading streamdb: {}", path);
        this.source = BinarySource.open(path);
        this.decompressor = decompressor;

        var streamDb = StreamDb.read(source);

        var entries = new HashMap<Long, StreamDbEntry>();
        for (var i = 0; i < streamDb.identities().length(); i++) {
            var identity = streamDb.identities().get(i);
            var entry = streamDb.entries().get(i);
            entries.put(identity, entry);
        }
        this.index = Map.copyOf(entries);
    }

    @Override
    public Optional<StreamDbEntry> get(Long key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<StreamDbEntry> getAll() {
        return index.values().stream();
    }

    @Override
    public Bytes read(Long key, Integer size) throws IOException {
        var entry = index.get(key);
        Check.state(entry != null, () -> "Stream not found: " + key);

        var decompressor = switch (entry.compressionMode()) {
            case STREAMER_COMPRESSION_NONE_IMAGE, STREAMER_COMPRESSION_NONE_MODEL -> Decompressor.none();
            case STREAMER_COMPRESSION_KRAKEN_IMAGE, STREAMER_COMPRESSION_KRAKEN_MODEL -> this.decompressor;
            default -> throw new IllegalStateException("Unexpected compression mode: " + entry.compressionMode());
        };

        source.position(entry.offset());
        var compressed = source.readBytes(entry.length());
        return decompressor.decompress(compressed, size);
    }

    @Override
    public void close() throws IOException {
        if (source != null) {
            source.close();
            source = null;
        }
    }
}
