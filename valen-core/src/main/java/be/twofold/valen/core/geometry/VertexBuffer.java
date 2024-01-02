package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.nio.*;

public record VertexBuffer(
    ByteBuffer buffer,
    ElementType elementType,
    ComponentType componentType,
    boolean normalized,
    int offset,
    int length
) {
    public VertexBuffer {
        Check.fromIndexSize(offset, length, buffer.limit());
        Check.notNull(componentType, "componentType must not be null");
        Check.notNull(elementType, "elementType must not be null");
        int stride = elementType.size() * componentType.size();
        Check.argument(length % stride == 0, () -> "length must be a multiple of " + stride);
    }

    public VertexBuffer(
        ByteBuffer buffer,
        ElementType elementType,
        ComponentType componentType,
        boolean normalized
    ) {
        this(buffer, elementType, componentType, normalized, 0, buffer.limit());
    }

    public int count() {
        return length / stride();
    }

    public int stride() {
        return elementType.size() * componentType.size();
    }
}
