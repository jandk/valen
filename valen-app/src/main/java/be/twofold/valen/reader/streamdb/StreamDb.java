package be.twofold.valen.reader.streamdb;

import java.util.*;

public record StreamDb(
    StreamDbHeader header,
    List<StreamDbEntry> entries,
    StreamDbPrefetchHeader prefetchHeader,
    List<StreamDbPrefetchBlock> prefetchBlocks,
    long[] prefetchIDs
) {
    @Override
    public String toString() {
        return "StreamDb[" +
               "header=" + header + ", " +
               "entries=" + entries.size() + " entries, " +
               "prefetchHeader=" + prefetchHeader + ", " +
               "prefetchBlocks=" + prefetchBlocks.size() + " blocks, " +
               "prefetchIDs=" + prefetchIDs.length + " IDs" +
               "]";
    }
}
