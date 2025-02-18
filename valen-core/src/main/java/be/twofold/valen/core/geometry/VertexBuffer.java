package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.nio.*;

public record VertexBuffer(
    Buffer buffer,
    ElementType elementType,
    ComponentType<?> componentType,
    boolean normalized
) {
    public VertexBuffer {
        Check.notNull(componentType, "componentType must not be null");
        Check.notNull(elementType, "elementType must not be null");
        Check.argument(buffer.limit() % elementType.size() == 0, () -> "length must be a multiple of " + elementType.size());
    }

    public VertexBuffer(Buffer buffer, VertexBuffer.Info<?> info) {
        this(buffer, info.elementType(), info.componentType(), info.normalized());
    }

    public int count() {
        return buffer.limit() / elementType.size();
    }

    public record Info<T extends Buffer>(
        Semantic semantic,
        ElementType elementType,
        ComponentType<T> componentType,
        boolean normalized
    ) {
        public static final Info<FloatBuffer> POSITION = new Info<>(Semantic.Position, ElementType.Vector3, ComponentType.Float, false);
        public static final Info<FloatBuffer> NORMAL = new Info<>(Semantic.Normal, ElementType.Vector3, ComponentType.Float, false);
        public static final Info<FloatBuffer> TANGENT = new Info<>(Semantic.Tangent, ElementType.Vector4, ComponentType.Float, false);

        public static <T extends Buffer> Info<T> colors(int n, ComponentType<T> componentType) {
            return new Info<>(new Semantic.Color(n), ElementType.Vector4, componentType, true);
        }

        public static <T extends Buffer> Info<T> joints(int n, ComponentType<T> componentType) {
            return new Info<>(new Semantic.Joints(n), ElementType.Vector4, componentType, false);
        }

        public static Info<FloatBuffer> texCoords(int n) {
            return new Info<>(new Semantic.TexCoord(n), ElementType.Vector2, ComponentType.Float, false);
        }

        public static <T extends Buffer> Info<T> weights(int n, ComponentType<T> componentType) {
            return new Info<>(new Semantic.Weights(n), ElementType.Vector4, componentType, true);
        }

        public static <T extends Buffer> Info<T> indices(ComponentType<T> componentType) {
            return new Info<>(null, ElementType.Scalar, componentType, false);
        }
    }
}
