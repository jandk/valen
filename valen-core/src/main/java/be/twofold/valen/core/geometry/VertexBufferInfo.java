package be.twofold.valen.core.geometry;

import java.nio.*;

public record VertexBufferInfo<T extends Buffer>(
    Semantic semantic,
    ElementType elementType,
    ComponentType<T> componentType,
    boolean normalized
) {
    public static final VertexBufferInfo<FloatBuffer> POSITION = new VertexBufferInfo<>(Semantic.POSITION, ElementType.VECTOR3, ComponentType.FLOAT, false);
    public static final VertexBufferInfo<FloatBuffer> NORMAL = new VertexBufferInfo<>(Semantic.NORMAL, ElementType.VECTOR3, ComponentType.FLOAT, false);
    public static final VertexBufferInfo<FloatBuffer> TANGENT = new VertexBufferInfo<>(Semantic.TANGENT, ElementType.VECTOR4, ComponentType.FLOAT, false);

    public static <T extends Buffer> VertexBufferInfo<T> colors(int n, ComponentType<T> componentType) {
        return new VertexBufferInfo<>(new Semantic.Color(n), ElementType.VECTOR4, componentType, true);
    }

    public static <T extends Buffer> VertexBufferInfo<T> joints(int n, ComponentType<T> componentType) {
        return new VertexBufferInfo<>(new Semantic.Joints(n), ElementType.VECTOR4, componentType, false);
    }

    public static VertexBufferInfo<FloatBuffer> texCoords(int n) {
        return new VertexBufferInfo<>(new Semantic.TexCoord(n), ElementType.VECTOR2, ComponentType.FLOAT, false);
    }

    public static <T extends Buffer> VertexBufferInfo<T> weights(int n, ComponentType<T> componentType) {
        return new VertexBufferInfo<>(new Semantic.Weights(n), ElementType.VECTOR4, componentType, true);
    }

    public static <T extends Buffer> VertexBufferInfo<T> indices(ComponentType<T> componentType) {
        return new VertexBufferInfo<>(null, ElementType.SCALAR, componentType, false);
    }
}
