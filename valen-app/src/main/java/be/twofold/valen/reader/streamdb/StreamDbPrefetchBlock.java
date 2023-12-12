package be.twofold.valen.reader.streamdb;

import be.twofold.valen.core.util.*;

public record StreamDbPrefetchBlock(
    long name,
    int firstItemIndex,
    int numItems
) {
    public static final int BYTES = 16;

    public static StreamDbPrefetchBlock read(BetterBuffer buffer) {
        var name = buffer.getLong();
        var firstItemIndex = buffer.getInt();
        var numItems = buffer.getInt();
        return new StreamDbPrefetchBlock(name, firstItemIndex, numItems);
    }
}
