package be.twofold.valen.core.geometry;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

public record VertexBuffer<T extends Slice>(
    T array,
    int length,
    ElementType elementType,
    ComponentType<T> componentType
) {
    public VertexBuffer {
        Check.nonNull(componentType, "componentType");
        Check.nonNull(elementType, "elementType");
        Check.nonNull(array, "array");
    }

    public int count() {
        return length * elementType().count();
    }
}
