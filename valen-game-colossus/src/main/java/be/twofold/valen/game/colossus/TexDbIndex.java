package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.colossus.reader.texdb.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

record TexDbIndex(
    Map<Path, BinarySource> sources,
    Map<Long, List<Location.FileSlice>> index
) {
    private static final Logger log = LoggerFactory.getLogger(TexDbIndex.class);

    TexDbIndex {
        sources = Map.copyOf(sources);
        index = index.entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> List.copyOf(e.getValue())));
    }

    static TexDbIndex build(List<Path> paths) throws IOException {
        var sources = new HashMap<Path, BinarySource>();
        var index = new HashMap<Long, List<Location.FileSlice>>();
        for (var path : paths) {
            log.info("Loading TexDb: {}", path);

            var source = BinarySource.open(path);
            sources.put(path, source);

            var localIndex = new LinkedHashMap<Long, List<Location.FileSlice>>();
            for (var entry : TexDb.read(source).entries()) {
                localIndex.computeIfAbsent(entry.hash(), _ -> new ArrayList<>())
                    .add(new Location.FileSlice(path, entry.offset(), -1));
            }
            index.putAll(localIndex);
        }

        return new TexDbIndex(sources, index);
    }
}
