package be.twofold.valen.core.util;

import be.twofold.valen.core.geometry.*;

import java.nio.*;

public final class Buffers {
    private Buffers() {
    }

    public static Buffer allocate(int count, ComponentType type) {
        return switch (type) {
            case Byte, UnsignedByte -> allocate(count);
            case Short, UnsignedShort -> allocate(count * Short.BYTES).asShortBuffer();
            case UnsignedInt -> allocate(count * Integer.BYTES).asIntBuffer();
            case Float -> allocate(count * Float.BYTES).asFloatBuffer();
        };
    }

    private static ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocate(capacity)
            .order(ByteOrder.LITTLE_ENDIAN);
    }
}
