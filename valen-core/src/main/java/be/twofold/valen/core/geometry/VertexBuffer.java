package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

public record VertexBuffer<T extends WrappedArray>(
    T array,
    int length,
    ElementType elementType,
    ComponentType<T> componentType
) {
    public VertexBuffer {
        Check.notNull(componentType, "componentType");
        Check.notNull(elementType, "elementType");
        Check.notNull(array, "array");
    }

    @SuppressWarnings("unchecked")
    public VertexBuffer(T array, GeoBufferInfo<?> bufferInfo) {
        this(array, bufferInfo.length(), bufferInfo.elementType(), (ComponentType<T>) bufferInfo.componentType());
    }

    public int count() {
        return length * elementType().count();
    }
}
