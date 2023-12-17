package be.twofold.valen.reader.streamdb;

import be.twofold.valen.core.util.*;

public record StreamDbPrefetchHeader(
    int numPrefetchBlocks,
    int totalLength
) {
    public static final int BYTES = 8;

    public static StreamDbPrefetchHeader read(BetterBuffer buffer) {
        var numPrefetchBlocks = buffer.getInt();
        var totalLength = buffer.getInt();
        return new StreamDbPrefetchHeader(numPrefetchBlocks, totalLength);
    }
}
