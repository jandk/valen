package be.twofold.valen.game.colossus.texdb;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.colossus.reader.texdb.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class TexDbFile implements Container<Long, TexDbEntry> {
    private static final Logger log = LoggerFactory.getLogger(TexDbFile.class);
    private final Map<Long, List<TexDbEntry>> index;
    private BinarySource source;

    public TexDbFile(Path path) throws IOException {
        log.info("Loading TexDB: {}", path);
        this.source = BinarySource.open(path);

        var entries = TexDb.read(source).entries();
        var index = entries.stream()
            .collect(Collectors.groupingBy(
                TexDbEntry::hash,
                Collectors.toUnmodifiableList()
            ));
        this.index = Map.copyOf(index);
    }

    @Override
    public Optional<TexDbEntry> get(Long key) {
        return Optional.ofNullable(index.get(key))
            .map(List::getFirst);
    }

    @Override
    public Stream<TexDbEntry> getAll() {
        return index.values().stream()
            .flatMap(List::stream);
    }

    @Override
    public Bytes read(Long key, Integer size) throws IOException {
        var entry = index.get(key);
        Check.state(entry != null, () -> "Tex not found: " + key);

        source.position(entry.getFirst().offset());
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
