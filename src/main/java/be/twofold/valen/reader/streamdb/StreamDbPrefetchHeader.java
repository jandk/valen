package be.twofold.valen.reader.streamdb;

import be.twofold.valen.*;

public record StreamDbPrefetchHeader(
    int numPrefetchBlocks,
    int totalLength
) {
    static final int Size = 0x08;

    public static StreamDbPrefetchHeader read(BetterBuffer buffer) {
        int numPrefetchBlocks = buffer.getInt();
        int totalLength = buffer.getInt();
        return new StreamDbPrefetchHeader(numPrefetchBlocks, totalLength);
    }
}
