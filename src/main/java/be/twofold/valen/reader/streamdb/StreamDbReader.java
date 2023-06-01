package be.twofold.valen.reader.streamdb;

import be.twofold.valen.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public final class StreamDbReader {
    private final SeekableByteChannel channel;

    private StreamDbHeader header;
    private List<StreamDbEntry> entries;
    private StreamDbPrefetchHeader prefetchHeader;
    private List<StreamDbPrefetchBlock> prefetchBlocks;
    private long[] prefetchIDs;

    public StreamDbReader(SeekableByteChannel channel) {
        this.channel = channel;
    }

    public StreamDb read() throws IOException {
        return read(false);
    }

    public StreamDb read(boolean prefetch) throws IOException {
        readIndex();

        if (prefetch) {
            readPrefetch();
            if (channel.position() != header.headerLength()) {
                throw new IOException("Header length does not match position");
            }
        }

        return new StreamDb(header, entries, prefetchHeader, prefetchBlocks, prefetchIDs);
    }

    private void readIndex() throws IOException {
        header = IOUtils.readStruct(channel, StreamDbHeader.Size, StreamDbHeader::read);
        entries = IOUtils.readStructs(channel, header.numEntries(), StreamDbEntry.Size, StreamDbEntry::read);
    }

    private void readPrefetch() throws IOException {
        prefetchHeader = IOUtils.readStruct(channel, StreamDbPrefetchHeader.Size, StreamDbPrefetchHeader::read);
        prefetchBlocks = IOUtils.readStructs(channel, prefetchHeader.numPrefetchBlocks(), StreamDbPrefetchBlock.Size, StreamDbPrefetchBlock::read);

        int numPrefetchIDs = prefetchBlocks.stream()
            .mapToInt(StreamDbPrefetchBlock::numItems)
            .sum();
        prefetchIDs = IOUtils.readLongs(channel, numPrefetchIDs);
    }
}
