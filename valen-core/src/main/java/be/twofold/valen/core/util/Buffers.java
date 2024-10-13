package be.twofold.valen.core.util;

import java.nio.*;
import java.util.*;
import java.util.function.*;

public final class Buffers {
    private static final Map<Class<? extends Buffer>, IntFunction<? extends Buffer>> FACTORIES = Map.of(
        ByteBuffer.class, Buffers::allocate,
        CharBuffer.class, capacity -> allocate(capacity * Character.BYTES).asCharBuffer(),
        DoubleBuffer.class, capacity -> allocate(capacity * Double.BYTES).asDoubleBuffer(),
        FloatBuffer.class, capacity -> allocate(capacity * Float.BYTES).asFloatBuffer(),
        IntBuffer.class, capacity -> allocate(capacity * Integer.BYTES).asIntBuffer(),
        LongBuffer.class, capacity -> allocate(capacity * Long.BYTES).asLongBuffer(),
        ShortBuffer.class, capacity -> allocate(capacity * Short.BYTES).asShortBuffer()
    );

    private Buffers() {
    }

    public static ByteBuffer allocate(int capacity) {
        return ByteBuffer
            .allocate(capacity)
            .order(ByteOrder.LITTLE_ENDIAN);
    }

    public static <T extends Buffer> T create(Class<T> type, int capacity) {
        return type.cast(FACTORIES.get(type).apply(capacity));
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
