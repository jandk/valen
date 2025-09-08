package be.twofold.valen.core.util;

import org.slf4j.*;

import java.nio.*;

public final class Buffers {
    private static final Logger log = LoggerFactory.getLogger(Buffers.class);

    private Buffers() {
    }

    public static ByteBuffer allocate(int capacity) {
        return ByteBuffer
            .allocate(capacity)
            .order(ByteOrder.LITTLE_ENDIAN);
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

        // log.warn("Actually copying buffer of size {}", buffer.remaining());
        var bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }
}
