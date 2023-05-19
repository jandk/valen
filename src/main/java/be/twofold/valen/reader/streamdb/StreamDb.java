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
        StringBuilder builder = new StringBuilder();
        builder.append("StreamDb[");
        builder.append("header=").append(header).append(", ");
        builder.append("entries=").append(entries.size()).append(" entries, ");
        if (prefetchHeader != null) {
            builder.append("prefetchHeader=").append(prefetchHeader).append(", ");
            builder.append("prefetchBlocks=").append(prefetchBlocks.size()).append(" blocks, ");
            builder.append("prefetchIDs=").append(prefetchIDs.length).append(" IDs");
        }
        builder.append("]");
        return builder.toString();
    }
}
