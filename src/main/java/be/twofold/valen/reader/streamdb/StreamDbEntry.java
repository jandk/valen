package be.twofold.valen.reader.streamdb;

import be.twofold.valen.*;

public record StreamDbEntry(
    long identity,
    int offset16,
    int length
) {
    static final int Size = 0x10;

    public static StreamDbEntry read(BetterBuffer buffer) {
        long identity = buffer.getLong();
        int offset16 = buffer.getInt();
        int length = buffer.getInt();
        return new StreamDbEntry(identity, offset16, length);
    }

    public long offset() {
        return offset16 * 16L;
    }
}
