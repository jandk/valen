package be.twofold.valen.reader.streamdb;

import java.nio.*;

public record StreamDbEntry(
    long identity,
    int offset16,
    int length
) {
    static final int Size = 0x10;

    public static StreamDbEntry read(ByteBuffer buffer) {
        long identity = buffer.getLong(0x00);
        int offset16 = buffer.getInt(0x08);
        int length = buffer.getInt(0x0c);
        return new StreamDbEntry(identity, offset16, length);
    }

    public long offset() {
        return offset16 * 16L;
    }
}
