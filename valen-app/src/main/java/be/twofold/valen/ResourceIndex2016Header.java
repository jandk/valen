package be.twofold.valen;

import be.twofold.valen.core.util.*;

public record ResourceIndex2016Header(
    int magic,
    int size
) {
    public static ResourceIndex2016Header read(BetterBuffer buffer) {
        int magic = buffer.getInt();
        int size = Integer.reverseBytes(buffer.getInt());
        buffer.expectLong(0);
        buffer.expectLong(0);
        buffer.expectLong(0);
        return new ResourceIndex2016Header(magic, size);
    }

    public int version() {
        return magic & 0xff;
    }
}
