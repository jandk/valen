package be.twofold.valen.core.geometry;

import be.twofold.valen.core.geometry.read.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

/**
 * The storage layout of a single vertex attribute.
 * <p>
 * Shared by {@link Accessor} and {@link VertexBuffer}.
 *
 * @param length        How many elements per vertex
 * @param elementType   The shape of each element ([ElementType])
 * @param componentType The type of each element ([ComponentType])
 * @param <T>           The type of each element
 */
public record AttributeLayout<T extends Slice>(
    int length,
    ElementType elementType,
    ComponentType<T> componentType
) {
    public AttributeLayout {
        Check.positive(length, "length");
        Check.nonNull(elementType, "elementType");
        Check.nonNull(componentType, "componentType");
    }

    public int count() {
        return length * elementType.count();
    }

    public T allocate(int capacity) {
        return componentType.allocate(capacity);
    }
}
