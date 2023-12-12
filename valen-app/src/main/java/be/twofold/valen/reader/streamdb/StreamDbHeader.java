package be.twofold.valen.reader.streamdb;

import be.twofold.valen.core.util.*;

public record StreamDbHeader(
    int length,
    int numEntries
) {
    public static final int BYTES = 32;

    public static StreamDbHeader read(BetterBuffer buffer) {
        buffer.expectLong(0x61c7f32e29c2a550L); // magic
        var length = buffer.getInt();
        buffer.expectInt(0); // padding
        buffer.expectInt(0); // padding
        buffer.expectInt(0); // padding
        var numEntries = buffer.getInt();
        buffer.expectInt(3); // flags
        return new StreamDbHeader(length, numEntries);
    }
}
