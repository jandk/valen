package be.twofold.valen.game.eternal.reader.streamdb;

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
    public static StreamDb read(BinaryReader reader) throws IOException {
        var header = StreamDbHeader.read(reader);
        var entries = reader.readObjects(header.numEntries(), StreamDbEntry::read);
        if (!header.flags().contains(StreamDbHeaderFlag.SDHF_HAS_PREFETCH_BLOCKS)) {
            return new StreamDb(header, entries, null, List.of(), new long[0]);
        }

        var prefetchHeader = StreamDbPrefetchHeader.read(reader);
        var prefetchBlocks = reader.readObjects(prefetchHeader.numPrefetchBlocks(), StreamDbPrefetchBlock::read);

        var numPrefetchIDs = prefetchBlocks.stream()
            .mapToInt(StreamDbPrefetchBlock::numItems)
            .sum();
        var prefetchIDs = reader.readLongs(numPrefetchIDs);
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
