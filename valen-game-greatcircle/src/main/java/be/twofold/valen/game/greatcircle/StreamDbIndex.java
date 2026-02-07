package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.greatcircle.reader.streamdb.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

record StreamDbIndex(
    Map<FileId, BinarySource> sources,
    Map<Long, Location.FileSlice> index
) {
    private static final Logger log = LoggerFactory.getLogger(StreamDbIndex.class);

    StreamDbIndex {
        sources = Map.copyOf(sources);
        index = Map.copyOf(index);
    }

    static StreamDbIndex build(Path base, List<String> paths) throws IOException {
        var sources = new HashMap<FileId, BinarySource>();
        var index = new HashMap<Long, Location.FileSlice>();
        for (var path : paths) {
            log.info("Loading StreamDb: {}", path);

            var fileId = new FileId(path);
            var source = BinarySource.open(base.resolve(path));
            sources.put(fileId, source);

            StreamDb streamDb = StreamDb.read(source);
            int length = streamDb.entries().size();
            for (int i = 0; i < length; i++) {
                var identity = streamDb.identities().get(i);
                var entry = streamDb.entries().get(i);

                // TODO: Wrap compression
                index.computeIfAbsent(identity, _ -> new Location.FileSlice(
                    fileId, entry.offset16() * 16L, entry.length()
                ));
            }
        }

        return new StreamDbIndex(sources, index);
    }
}
