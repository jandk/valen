package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.util.function.*;

/**
 * Type of data component in the buffer, for now the same as the GLTF type.
 */
public final class ComponentType<T> {
    public static final ComponentType<MutableBytes> UNSIGNED_BYTE = new ComponentType<>(MutableBytes::allocate);
    public static final ComponentType<MutableShorts> UNSIGNED_SHORT = new ComponentType<>(MutableShorts::allocate);
    public static final ComponentType<MutableInts> UNSIGNED_INT = new ComponentType<>(MutableInts::allocate);
    public static final ComponentType<MutableFloats> FLOAT = new ComponentType<>(MutableFloats::allocate);

    private final IntFunction<T> allocator;

    private ComponentType(IntFunction<T> allocator) {
        this.allocator = Check.notNull(allocator, "allocator");
    }

    public T allocate(int capacity) {
        return allocator.apply(capacity);
    }
}
