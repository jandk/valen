package be.twofold.valen.core.geometry.read;

import be.twofold.valen.core.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class MeshInfo {
    private final int indexCount;
    private final int vertexCount;
    private final BufferInfo<Ints.Mutable> indices;
    private final BufferInfo<Floats.Mutable> positions;
    private final BufferInfo<Floats.Mutable> normals;
    private final BufferInfo<Floats.Mutable> tangents;
    private final List<BufferInfo<Floats.Mutable>> texCoords;
    private final List<BufferInfo<Bytes.Mutable>> colors;
    private final BufferInfo<Shorts.Mutable> joints;
    private final BufferInfo<Floats.Mutable> weights;
    private final Map<String, BufferInfo<?>> custom;

    public MeshInfo(
        int indexCount,
        int vertexCount,
        BufferInfo<Ints.Mutable> indices,
        BufferInfo<Floats.Mutable> positions,
        BufferInfo<Floats.Mutable> normals,
        BufferInfo<Floats.Mutable> tangents,
        List<BufferInfo<Floats.Mutable>> texCoords,
        List<BufferInfo<Bytes.Mutable>> colors,
        BufferInfo<Shorts.Mutable> joints,
        BufferInfo<Floats.Mutable> weights,
        Map<String, BufferInfo<?>> custom
    ) {
        this.indexCount = indexCount;
        this.vertexCount = vertexCount;
        this.indices = Check.nonNull(indices, "indices");
        this.positions = Check.nonNull(positions, "positions");
        this.normals = normals;
        this.tangents = tangents;
        this.texCoords = List.copyOf(texCoords);
        this.colors = List.copyOf(colors);
        this.joints = joints;
        this.weights = weights;
        this.custom = Map.copyOf(custom);
    }

    public static Builder builder(int indexCount, int vertexCount) {
        return new Builder(indexCount, vertexCount);
    }

    public int indexCount() {
        return indexCount;
    }

    public BufferInfo<Ints.Mutable> indices() {
        return indices;
    }

    public int vertexCount() {
        return vertexCount;
    }

    public BufferInfo<Floats.Mutable> positions() {
        return positions;
    }

    public Optional<BufferInfo<Floats.Mutable>> normals() {
        return Optional.ofNullable(normals);
    }

    public Optional<BufferInfo<Floats.Mutable>> tangents() {
        return Optional.ofNullable(tangents);
    }

    public List<BufferInfo<Floats.Mutable>> texCoords() {
        return texCoords;
    }

    public List<BufferInfo<Bytes.Mutable>> colors() {
        return colors;
    }

    public Optional<BufferInfo<Shorts.Mutable>> joints() {
        return Optional.ofNullable(joints);
    }

    public Optional<BufferInfo<Floats.Mutable>> weights() {
        return Optional.ofNullable(weights);
    }

    public Map<String, BufferInfo<?>> custom() {
        return custom;
    }

    public static final class Builder {
        private final int indexCount;
        private final int vertexCount;

        private BufferInfo<Ints.Mutable> indices;
        private BufferInfo<Floats.Mutable> positions;
        private BufferInfo<Floats.Mutable> normals;
        private BufferInfo<Floats.Mutable> tangents;
        private final List<BufferInfo<Floats.Mutable>> texCoords = new ArrayList<>();
        private final List<BufferInfo<Bytes.Mutable>> colors = new ArrayList<>();
        private BufferInfo<Shorts.Mutable> joints;
        private BufferInfo<Floats.Mutable> weights;
        private final Map<String, BufferInfo<?>> custom = new HashMap<>();

        private Builder(int indexCount, int vertexCount) {
            this.indexCount = Check.positiveOrZero(indexCount, "indexCount");
            this.vertexCount = Check.positiveOrZero(vertexCount, "vertexCount");
        }

        public Builder indices(int offset, int stride, AttributeReader<Ints.Mutable> reader) {
            this.indices = new BufferInfo<>(offset, stride, reader, new AttributeLayout<>(1, ElementType.SCALAR, ComponentType.UNSIGNED_INT));
            return this;
        }

        public Builder positions(int offset, int stride, AttributeReader<Floats.Mutable> reader) {
            this.positions = new BufferInfo<>(offset, stride, reader, new AttributeLayout<>(1, ElementType.VECTOR3, ComponentType.FLOAT));
            return this;
        }

        public Builder normals(int offset, int stride, AttributeReader<Floats.Mutable> reader) {
            this.normals = new BufferInfo<>(offset, stride, reader, new AttributeLayout<>(1, ElementType.VECTOR3, ComponentType.FLOAT));
            return this;
        }

        public Builder tangents(int offset, int stride, AttributeReader<Floats.Mutable> reader) {
            this.tangents = new BufferInfo<>(offset, stride, reader, new AttributeLayout<>(1, ElementType.VECTOR4, ComponentType.FLOAT));
            return this;
        }

        public Builder addTexCoords(int offset, int stride, AttributeReader<Floats.Mutable> reader) {
            this.texCoords.add(new BufferInfo<>(offset, stride, reader, new AttributeLayout<>(1, ElementType.VECTOR2, ComponentType.FLOAT)));
            return this;
        }

        public Builder addColors(int offset, int stride, AttributeReader<Bytes.Mutable> reader) {
            this.colors.add(new BufferInfo<>(offset, stride, reader, new AttributeLayout<>(1, ElementType.VECTOR4, ComponentType.UNSIGNED_BYTE)));
            return this;
        }

        public Builder joints(int offset, int stride, int length, AttributeReader<Shorts.Mutable> reader) {
            this.joints = new BufferInfo<>(offset, stride, reader, new AttributeLayout<>(length, ElementType.SCALAR, ComponentType.UNSIGNED_SHORT));
            return this;
        }

        public Builder weights(int offset, int stride, int length, AttributeReader<Floats.Mutable> reader) {
            this.weights = new BufferInfo<>(offset, stride, reader, new AttributeLayout<>(length, ElementType.SCALAR, ComponentType.FLOAT));
            return this;
        }

        public <T extends Slice> Builder custom(String name, int offset, int stride, int length, AttributeReader<T> reader, ComponentType<T> componentType, ElementType elementType) {
            this.custom.put(name, new BufferInfo<>(offset, stride, reader, new AttributeLayout<>(length, elementType, componentType)));
            return this;
        }

        public MeshInfo build() {
            return new MeshInfo(indexCount, vertexCount, indices, positions, normals, tangents, texCoords, colors, joints, weights, custom);
        }
    }
}
