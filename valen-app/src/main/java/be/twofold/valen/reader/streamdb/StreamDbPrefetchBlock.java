package be.twofold.valen.reader.streamdb;

import be.twofold.valen.core.util.*;

public record StreamDbPrefetchBlock(
    long name,
    int firstItemIndex,
    int numItems
) {
    static final int Size = 0x10;

    public static StreamDbPrefetchBlock read(BetterBuffer buffer) {
        long name = buffer.getLong();
        int firstItemIndex = buffer.getInt();
        int numItems = buffer.getInt();
        return new StreamDbPrefetchBlock(name, firstItemIndex, numItems);
    }
}
