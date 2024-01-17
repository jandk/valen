package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.nio.*;

public record VertexBuffer(
    Buffer buffer,
    ElementType elementType,
    ComponentType componentType,
    boolean normalized
) {
    public VertexBuffer {
        Check.notNull(componentType, "componentType must not be null");
        Check.notNull(elementType, "elementType must not be null");
        Check.argument(buffer.limit() % elementType.size() == 0, () -> "length must be a multiple of " + elementType.size());
    }

    public int count() {
        return buffer.limit() / elementType.size();
    }
}
