package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.greatcircle.reader.streamdb.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

record StreamDbIndex(
    Map<Path, BinarySource> sources,
    Map<Long, Location> index
) {
    static final int UNKNOWN_SIZE = 0;

    private static final Logger log = LoggerFactory.getLogger(StreamDbIndex.class);

    StreamDbIndex {
        sources = Map.copyOf(sources);
        index = Map.copyOf(index);
    }

    static StreamDbIndex build(List<Path> paths) throws IOException {
        var sources = new HashMap<Path, BinarySource>();
        var index = new HashMap<Long, Location>();
        for (var path : paths) {
            log.info("Loading StreamDb: {}", path);

            var source = BinarySource.open(path);
            sources.put(path, source);

            StreamDb streamDb = StreamDb.read(source);
            int length = streamDb.entries().size();
            for (int i = 0; i < length; i++) {
                var identity = streamDb.identities().get(i);
                var entry = streamDb.entries().get(i);

                index.computeIfAbsent(identity, _ -> {
                    Location slice = new Location.FileSlice(path, entry.offset16() * 16L, entry.length());
                    return entry.compressionType().compressed()
                        ? new Location.Compressed(slice, CompressionType.OODLE, UNKNOWN_SIZE)
                        : slice;
                });
            }
        }

        return new StreamDbIndex(sources, index);
    }
}
