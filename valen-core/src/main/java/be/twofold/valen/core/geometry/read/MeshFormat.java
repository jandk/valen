package be.twofold.valen.core.geometry.read;

import be.twofold.valen.core.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

/**
 * Read-time version of Mesh.
 * <p>
 * Contains Accessors to know how to read the different VertexBuffers.
 */
public record MeshFormat(
    int indexCount,
    int vertexCount,
    Accessor<Ints.Mutable> indices,
    Map<Semantic, Accessor<?>> accessors
) {
    public MeshFormat {
        Check.positiveOrZero(indexCount, "indexCount");
        Check.positiveOrZero(vertexCount, "vertexCount");
        Check.nonNull(indices, "indices");
        accessors = Collections.unmodifiableMap(new LinkedHashMap<>(accessors));
    }

    public static Builder builder(int indexCount, int vertexCount) {
        return new Builder(indexCount, vertexCount);
    }

    public static final class Builder {
        private final int indexCount;
        private final int vertexCount;
        private Accessor<Ints.Mutable> indices;
        private final Map<Semantic, Accessor<?>> accessors = new LinkedHashMap<>();
        private int texCoordCount = 0;
        private int colorCount = 0;

        private Builder(int indexCount, int vertexCount) {
            this.indexCount = Check.positiveOrZero(indexCount, "indexCount");
            this.vertexCount = Check.positiveOrZero(vertexCount, "vertexCount");
        }

        public Builder indices(int offset, int stride, AttributeReader<Ints.Mutable> reader) {
            this.indices = new Accessor<>(offset, stride, reader, new AttributeLayout<>(1, ElementType.SCALAR, ComponentType.UNSIGNED_INT));
            return this;
        }

        public Builder positions(int offset, int stride, AttributeReader<Floats.Mutable> reader) {
            return accessor(Semantic.POSITION, offset, stride, reader, 1, ElementType.VECTOR3, ComponentType.FLOAT);
        }

        public Builder normals(int offset, int stride, AttributeReader<Floats.Mutable> reader) {
            return accessor(Semantic.NORMAL, offset, stride, reader, 1, ElementType.VECTOR3, ComponentType.FLOAT);
        }

        public Builder tangents(int offset, int stride, AttributeReader<Floats.Mutable> reader) {
            return accessor(Semantic.TANGENT, offset, stride, reader, 1, ElementType.VECTOR4, ComponentType.FLOAT);
        }

        public Builder addTexCoords(int offset, int stride, AttributeReader<Floats.Mutable> reader) {
            return accessor(new Semantic.TexCoord(texCoordCount++), offset, stride, reader, 1, ElementType.VECTOR2, ComponentType.FLOAT);
        }

        public Builder addColors(int offset, int stride, AttributeReader<Bytes.Mutable> reader) {
            return accessor(new Semantic.Color(colorCount++), offset, stride, reader, 1, ElementType.VECTOR4, ComponentType.UNSIGNED_BYTE);
        }

        public Builder joints(int offset, int stride, int length, AttributeReader<Shorts.Mutable> reader) {
            return accessor(Semantic.JOINTS, offset, stride, reader, length, ElementType.SCALAR, ComponentType.UNSIGNED_SHORT);
        }

        public Builder weights(int offset, int stride, int length, AttributeReader<Floats.Mutable> reader) {
            return accessor(Semantic.WEIGHTS, offset, stride, reader, length, ElementType.SCALAR, ComponentType.FLOAT);
        }

        public <T extends Slice> Builder custom(String name, int offset, int stride, int length, AttributeReader<T> reader, ComponentType<T> componentType, ElementType elementType) {
            return accessor(new Semantic.Custom(name), offset, stride, reader, length, elementType, componentType);
        }

        public MeshFormat build() {
            return new MeshFormat(indexCount, vertexCount, indices, accessors);
        }

        private <T extends Slice> Builder accessor(Semantic semantic, int offset, int stride, AttributeReader<T> reader, int length, ElementType elementType, ComponentType<T> componentType) {
            accessors.put(semantic, new Accessor<>(offset, stride, reader, new AttributeLayout<>(length, elementType, componentType)));
            return this;
        }
    }
}
