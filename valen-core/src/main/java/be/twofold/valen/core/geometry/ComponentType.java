package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.function.*;

/**
 * Type of data component in the buffer, for now the same as the GLTF type.
 */
public final class ComponentType<T extends Buffer> {
    public static final ComponentType<ByteBuffer> BYTE = new ComponentType<>(Byte.BYTES, ByteBuffer::allocate);
    public static final ComponentType<ByteBuffer> UNSIGNED_BYTE = new ComponentType<>(Byte.BYTES, ByteBuffer::allocate);
    public static final ComponentType<ShortBuffer> SHORT = new ComponentType<>(Short.BYTES, ShortBuffer::allocate);
    public static final ComponentType<ShortBuffer> UNSIGNED_SHORT = new ComponentType<>(Short.BYTES, ShortBuffer::allocate);
    public static final ComponentType<IntBuffer> UNSIGNED_INT = new ComponentType<>(Integer.BYTES, IntBuffer::allocate);
    public static final ComponentType<FloatBuffer> FLOAT = new ComponentType<>(Float.BYTES, FloatBuffer::allocate);

    private final int size;
    private final IntFunction<T> allocator;

    private ComponentType(int size, IntFunction<T> allocator) {
        this.size = size;
        this.allocator = Check.notNull(allocator, "allocator");
    }

    public T allocate(int capacity) {
        return allocator.apply(capacity);
    }

    public int size() {
        return size;
    }
}
