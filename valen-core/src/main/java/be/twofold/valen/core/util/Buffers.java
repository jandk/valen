package be.twofold.valen.core.util;

import java.nio.*;

public final class Buffers {
    private Buffers() {
    }

    public static byte[] toArray(ByteBuffer buffer) {
        if (buffer.hasArray() &&
            buffer.position() == 0 &&
            buffer.limit() == buffer.capacity()
        ) {
            return buffer.array();
        }

        System.out.println("Actually copying buffer of size " + buffer.remaining());
        var bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }
}
