package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;
import java.util.function.*;

/**
 * Type of data component in the buffer, for now the same as the GLTF type.
 */
public final class ComponentType<T extends Buffer> {
    private static final Map<Class<? extends Buffer>, IntFunction<? extends Buffer>> factories = Map.of(
        ByteBuffer.class, ByteBuffer::allocate,
        ShortBuffer.class, ShortBuffer::allocate,
        IntBuffer.class, IntBuffer::allocate,
        FloatBuffer.class, FloatBuffer::allocate
    );

    public static final ComponentType<ByteBuffer> Byte = new ComponentType<>(1, ByteBuffer.class);
    public static final ComponentType<ByteBuffer> UnsignedByte = new ComponentType<>(1, ByteBuffer.class);
    public static final ComponentType<ShortBuffer> Short = new ComponentType<>(2, ShortBuffer.class);
    public static final ComponentType<ShortBuffer> UnsignedShort = new ComponentType<>(2, ShortBuffer.class);
    public static final ComponentType<IntBuffer> UnsignedInt = new ComponentType<>(4, IntBuffer.class);
    public static final ComponentType<FloatBuffer> Float = new ComponentType<>(4, FloatBuffer.class);

    private final int size;
    private final Class<T> bufferType;

    private ComponentType(int size, Class<T> bufferType) {
        this.size = size;
        this.bufferType = Check.notNull(bufferType);
    }

    public T allocate(int count) {
        return Buffers.create(bufferType, count);
    }

    public int size() {
        return size;
    }

    public Class<T> bufferType() {
        return bufferType;
    }
}
