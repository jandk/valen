package be.twofold.valen.reader.streamdb;

import be.twofold.valen.*;

public record StreamDbHeader(
    int headerLength,
    int numEntries
) {
    static final int Size = 0x20;

    public static StreamDbHeader read(BetterBuffer buffer) {
        buffer.expectLong(0x61c7f32e29c2a550L); // magic
        int headerLength = buffer.getInt();
        buffer.expectInt(0); // padding
        buffer.expectInt(0); // padding
        buffer.expectInt(0); // padding
        int numEntries = buffer.getInt();
        buffer.expectInt(3); // flags
        return new StreamDbHeader(headerLength, numEntries);
    }
}
