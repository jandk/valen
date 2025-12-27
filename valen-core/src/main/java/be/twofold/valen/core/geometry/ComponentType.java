package be.twofold.valen.core.geometry;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

import java.util.function.*;

/**
 * Type of data component in the buffer, for now the same as the GLTF type.
 */
public final class ComponentType<T> {
    public static final ComponentType<Bytes.Mutable> UNSIGNED_BYTE = new ComponentType<>(Bytes.Mutable::allocate);
    public static final ComponentType<Shorts.Mutable> UNSIGNED_SHORT = new ComponentType<>(Shorts.Mutable::allocate);
    public static final ComponentType<Ints.Mutable> UNSIGNED_INT = new ComponentType<>(Ints.Mutable::allocate);
    public static final ComponentType<Floats.Mutable> FLOAT = new ComponentType<>(Floats.Mutable::allocate);

    private final IntFunction<T> allocator;

    private ComponentType(IntFunction<T> allocator) {
        this.allocator = Check.nonNull(allocator, "allocator");
    }

    public T allocate(int capacity) {
        return allocator.apply(capacity);
    }
}
