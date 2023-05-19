package be.twofold.valen.reader.streamdb;

import java.nio.*;

public record StreamDbPrefetchBlock(
    long name,
    int firstItemIndex,
    int numItems
) {
    static final int Size = 0x10;

    public static StreamDbPrefetchBlock read(ByteBuffer buffer) {
        long name = buffer.getLong(0x00);
        int firstItemIndex = buffer.getInt(0x08);
        int numItems = buffer.getInt(0x0c);
        return new StreamDbPrefetchBlock(name, firstItemIndex, numItems);
    }
}
