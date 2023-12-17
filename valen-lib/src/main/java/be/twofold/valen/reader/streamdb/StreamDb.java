package be.twofold.valen.reader.streamdb;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public record StreamDb(
    StreamDbHeader header,
    List<StreamDbEntry> entries,
    StreamDbPrefetchHeader prefetchHeader,
    List<StreamDbPrefetchBlock> prefetchBlocks,
    long[] prefetchIDs
) {
    public static StreamDb read(SeekableByteChannel channel) throws IOException {
        var header = IOUtils.readStruct(channel, StreamDbHeader.BYTES, StreamDbHeader::read);
        var entries = IOUtils.readStructs(channel, header.numEntries(), StreamDbEntry.BYTES, StreamDbEntry::read);

        if (header.flags().contains(StreamDbHeaderFlag.SDHF_HAS_PREFETCH_BLOCKS)) {
            var prefetchHeader = IOUtils.readStruct(channel, StreamDbPrefetchHeader.BYTES, StreamDbPrefetchHeader::read);
            var prefetchBlocks = IOUtils.readStructs(channel, prefetchHeader.numPrefetchBlocks(), StreamDbPrefetchBlock.BYTES, StreamDbPrefetchBlock::read);

            var numPrefetchIDs = prefetchBlocks.stream().mapToInt(StreamDbPrefetchBlock::numItems).sum();
            var prefetchIDs = IOUtils.readLongs(channel, numPrefetchIDs);
            return new StreamDb(header, entries, prefetchHeader, prefetchBlocks, prefetchIDs);
        }

        return new StreamDb(header, entries, null, List.of(), new long[0]);
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
