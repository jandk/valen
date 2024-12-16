package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.function.*;

/**
 * Type of data component in the buffer, for now the same as the GLTF type.
 */
public final class ComponentType<T extends Buffer> {
    public static final ComponentType<ByteBuffer> Byte = new ComponentType<>(1, ByteBuffer::allocate);
    public static final ComponentType<ByteBuffer> UnsignedByte = new ComponentType<>(1, ByteBuffer::allocate);
    public static final ComponentType<ShortBuffer> Short = new ComponentType<>(2, ShortBuffer::allocate);
    public static final ComponentType<ShortBuffer> UnsignedShort = new ComponentType<>(2, ShortBuffer::allocate);
    public static final ComponentType<IntBuffer> UnsignedInt = new ComponentType<>(4, IntBuffer::allocate);
    public static final ComponentType<FloatBuffer> Float = new ComponentType<>(4, FloatBuffer::allocate);

    private final int size;
    private final IntFunction<T> allocator;

    private ComponentType(int size, IntFunction<T> allocator) {
        this.size = size;
        this.allocator = Check.notNull(allocator, "allocator");
    }

    public T allocate(int count) {
        return allocator.apply(count);
    }

    public int size() {
        return size;
    }
}
