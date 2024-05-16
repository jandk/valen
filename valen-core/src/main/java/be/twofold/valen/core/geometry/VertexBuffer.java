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

    public VertexBuffer(Buffer buffer, VertexBuffer.Info info) {
        this(buffer, info.elementType(), info.componentType(), info.normalized());
    }

    public int count() {
        return buffer.limit() / elementType.size();
    }

    public record Info(
        Semantic semantic,
        ElementType elementType,
        ComponentType componentType,
        boolean normalized
    ) {
        public int size() {
            return elementType.size();
        }
    }
}
