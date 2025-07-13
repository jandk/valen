package be.twofold.valen.game.darkages.reader.streamdb;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import org.slf4j.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class StreamDbFile implements Container<Long, StreamDbEntry> {
    private static final Logger log = LoggerFactory.getLogger(StreamDbFile.class);

    private final Map<Long, StreamDbEntry> index;
    private final Decompressor decompressor;
    private final Path path;
    private BinaryReader reader;

    public StreamDbFile(Path path, Decompressor decompressor) throws IOException {
        log.info("Loading streamdb: {}", path);
        this.decompressor = Check.notNull(decompressor);
        this.path = Check.notNull(path);
        this.reader = BinaryReader.fromPath(path);

        var entries = StreamDb.read(reader).entries();
        this.index = entries.stream()
            .collect(Collectors.toUnmodifiableMap(
                StreamDbEntry::identity,
                Function.identity()
            ));
    }

    @Override
    public Optional<StreamDbEntry> get(Long identity) {
        return Optional.ofNullable(index.get(identity));
    }

    @Override
    public Stream<StreamDbEntry> getAll() {
        return index.values().stream();
    }

    @Override
    public ByteBuffer read(Long key, Integer size) throws IOException {
        var entry = index.get(key);
        Check.state(entry != null, () -> "Stream not found: " + key);

        log.debug("Reading stream: {}", String.format("%016X", entry.identity()));
        reader.position(entry.offset());
        var compressed = reader.readBuffer(entry.length());
        if (size == null || compressed.remaining() == size) {
            return compressed;
        }

        return decompressor.decompress(compressed, size);
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }

    @Override
    public String toString() {
        return "StreamDbFile(" + path + ")";
    }
}
