package be.twofold.valen.core.geometry;

import java.nio.*;

public record VertexBufferInfo<T extends Buffer>(
    Semantic semantic,
    ComponentType<T> componentType,
    int size
) {
    public static final VertexBufferInfo<FloatBuffer> POSITION = new VertexBufferInfo<>(Semantic.POSITION, ComponentType.FLOAT, 3);
    public static final VertexBufferInfo<FloatBuffer> NORMAL = new VertexBufferInfo<>(Semantic.NORMAL, ComponentType.FLOAT, 3);
    public static final VertexBufferInfo<FloatBuffer> TANGENT = new VertexBufferInfo<>(Semantic.TANGENT, ComponentType.FLOAT, 4);
    public static final VertexBufferInfo<FloatBuffer> TEX_COORDS = new VertexBufferInfo<>(Semantic.TEX_COORD, ComponentType.FLOAT, 2);

    public static <T extends Buffer> VertexBufferInfo<T> colors(ComponentType<T> componentType) {
        return new VertexBufferInfo<>(Semantic.COLOR, componentType, 4);
    }

    public static <T extends Buffer> VertexBufferInfo<T> joints(ComponentType<T> componentType, int size) {
        return new VertexBufferInfo<>(Semantic.JOINTS, componentType, size);
    }

    public static <T extends Buffer> VertexBufferInfo<T> weights(ComponentType<T> componentType, int size) {
        return new VertexBufferInfo<>(Semantic.WEIGHTS, componentType, size);
    }

    public static <T extends Buffer> VertexBufferInfo<T> indices(ComponentType<T> componentType) {
        return new VertexBufferInfo<>(null, componentType, 1);
    }
}
