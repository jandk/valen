package be.twofold.valen.core.util;

import java.nio.*;

public final class Buffers {

    private Buffers() {
    }

    public static ByteBuffer allocate(int capacity) {
        return ByteBuffer
            .allocate(capacity)
            .order(ByteOrder.LITTLE_ENDIAN);
    }

    public static byte[] toArray(ByteBuffer buffer) {
        if (buffer.hasArray() &&
            buffer.position() == 0 &&
            buffer.limit() == buffer.capacity()
        ) {
            return buffer.array();
        }

        var bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }
}
