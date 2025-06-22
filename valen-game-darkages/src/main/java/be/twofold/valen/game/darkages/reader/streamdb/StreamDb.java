package be.twofold.valen.game.darkages.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StreamDb(
    StreamDbHeader header,
    List<StreamDbEntry> entries,
    StreamDbPrefetchHeader prefetchHeader,
    List<StreamDbPrefetchBlock> prefetchBlocks,
    long[] prefetchIDs
) {
    public static StreamDb read(DataSource source) throws IOException {
        var header = StreamDbHeader.read(source);
        var entries = source.readObjects(header.numEntries(), StreamDbEntry::read);
        if (!header.flags().contains(StreamDbHeaderFlag.SDHF_HAS_PREFETCH_BLOCKS)) {
            return new StreamDb(header, entries, null, List.of(), new long[0]);
        }

        var prefetchHeader = StreamDbPrefetchHeader.read(source);
        var prefetchBlocks = source.readObjects(prefetchHeader.numPrefetchBlocks(), StreamDbPrefetchBlock::read);

        var numPrefetchIDs = prefetchBlocks.stream()
            .mapToInt(StreamDbPrefetchBlock::numItems)
            .sum();
        var prefetchIDs = source.readLongs(numPrefetchIDs);
        return new StreamDb(header, entries, prefetchHeader, prefetchBlocks, prefetchIDs);
    }

    @Override
    public String toString() {
        return "StreamDb[" +
            "header=" + header + ", " +
            "entries=(" + entries.size() + " entries), " +
            "prefetchHeader=" + prefetchHeader + ", " +
            "prefetchBlocks=(" + prefetchBlocks.size() + " blocks), " +
            "prefetchIDs=(" + prefetchIDs.length + " IDs)" +
            "]";
    }
}
