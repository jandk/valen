package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.reader.streamdb.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

record StreamDbIndex(
    Map<Path, BinarySource> sources,
    Map<Long, Location.FileSlice> index
) {
    private static final Logger log = LoggerFactory.getLogger(StreamDbIndex.class);

    StreamDbIndex {
        sources = Map.copyOf(sources);
        index = Map.copyOf(index);
    }

    static StreamDbIndex build(List<Path> paths) throws IOException {
        var sources = new HashMap<Path, BinarySource>();
        var index = new HashMap<Long, Location.FileSlice>();
        for (var path : paths) {
            log.info("Loading StreamDb: {}", path);

            var source = BinarySource.open(path);
            sources.put(path, source);

            for (var entry : StreamDb.read(source).entries()) {
                index.computeIfAbsent(entry.identity(), _ -> new Location.FileSlice(
                    path, entry.offset16() * 16L, entry.length()
                ));
            }
        }

        return new StreamDbIndex(sources, index);
    }
}
