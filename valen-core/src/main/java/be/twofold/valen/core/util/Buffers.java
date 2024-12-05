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
}
