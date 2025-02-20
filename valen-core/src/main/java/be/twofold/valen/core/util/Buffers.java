package be.twofold.valen.core.util;

import java.nio.*;

public final class Buffers {
    private Buffers() {
    }

    public static void copy(ByteBuffer src, ByteBuffer dst) {
        copy(src, dst, dst.remaining());
    }

    public static void copy(ByteBuffer src, ByteBuffer dst, int length) {
        dst.put(src.slice().limit(length));
        src.position(src.position() + length);
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
