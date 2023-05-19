package be.twofold.valen.reader.streamdb;

import java.nio.*;

public record StreamDbHeader(
    int headerLength,
    int numEntries
) {
    private static final long Magic = 0x61c7f32e29c2a550L;
    static final int Size = 0x20;

    public static StreamDbHeader read(ByteBuffer buffer) {
        long magic = buffer.getLong(0x00);
        if (magic != Magic) {
            throw new IllegalArgumentException("Invalid magic, expected 0x%016x, got 0x%016x".formatted(Magic, magic));
        }
        int headerLength = buffer.getInt(0x08);
        int numEntries = buffer.getInt(0x18);
        return new StreamDbHeader(headerLength, numEntries);
    }
}
