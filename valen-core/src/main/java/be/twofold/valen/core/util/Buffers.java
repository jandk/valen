package be.twofold.valen.core.util;

import java.nio.*;

public final class Buffers {
    private Buffers() {
    }

    public static ByteBuffer allocateByte(int capacity) {
        return ByteBuffer.allocate(capacity)
            .order(ByteOrder.LITTLE_ENDIAN);
    }

    public static ByteBuffer allocateShort(int capacity) {
        return ByteBuffer.allocate(capacity * Short.BYTES)
            .order(ByteOrder.LITTLE_ENDIAN);
    }

    public static ByteBuffer allocateFloat(int capacity) {
        return ByteBuffer
            .allocate(capacity * Float.BYTES)
            .order(ByteOrder.LITTLE_ENDIAN);
    }
}
