package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.util.*;

public final class GeoMeshInfo {
    private final int indexCount;
    private final int vertexCount;
    private final GeoBufferInfo<Ints.Mutable> indices;
    private final GeoBufferInfo<Floats.Mutable> positions;
    private final GeoBufferInfo<Floats.Mutable> normals;
    private final GeoBufferInfo<Floats.Mutable> tangents;
    private final List<GeoBufferInfo<Floats.Mutable>> texCoords;
    private final List<GeoBufferInfo<Bytes.Mutable>> colors;
    private final GeoBufferInfo<Shorts.Mutable> joints;
    private final GeoBufferInfo<Floats.Mutable> weights;
    private final Map<String, GeoBufferInfo<?>> custom;

    public GeoMeshInfo(
        int indexCount,
        int vertexCount,
        GeoBufferInfo<Ints.Mutable> indices,
        GeoBufferInfo<Floats.Mutable> positions,
        GeoBufferInfo<Floats.Mutable> normals,
        GeoBufferInfo<Floats.Mutable> tangents,
        List<GeoBufferInfo<Floats.Mutable>> texCoords,
        List<GeoBufferInfo<Bytes.Mutable>> colors,
        GeoBufferInfo<Shorts.Mutable> joints,
        GeoBufferInfo<Floats.Mutable> weights,
        Map<String, GeoBufferInfo<?>> custom
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

    public GeoBufferInfo<Ints.Mutable> indices() {
        return indices;
    }

    public int vertexCount() {
        return vertexCount;
    }

    public GeoBufferInfo<Floats.Mutable> positions() {
        return positions;
    }

    public Optional<GeoBufferInfo<Floats.Mutable>> normals() {
        return Optional.ofNullable(normals);
    }

    public Optional<GeoBufferInfo<Floats.Mutable>> tangents() {
        return Optional.ofNullable(tangents);
    }

    public List<GeoBufferInfo<Floats.Mutable>> texCoords() {
        return texCoords;
    }

    public List<GeoBufferInfo<Bytes.Mutable>> colors() {
        return colors;
    }

    public Optional<GeoBufferInfo<Shorts.Mutable>> joints() {
        return Optional.ofNullable(joints);
    }

    public Optional<GeoBufferInfo<Floats.Mutable>> weights() {
        return Optional.ofNullable(weights);
    }

    public Map<String, GeoBufferInfo<?>> custom() {
        return custom;
    }

    public static final class Builder {
        private final int indexCount;
        private final int vertexCount;

        private GeoBufferInfo<Ints.Mutable> indices;
        private GeoBufferInfo<Floats.Mutable> positions;
        private GeoBufferInfo<Floats.Mutable> normals;
        private GeoBufferInfo<Floats.Mutable> tangents;
        private final List<GeoBufferInfo<Floats.Mutable>> texCoords = new ArrayList<>();
        private final List<GeoBufferInfo<Bytes.Mutable>> colors = new ArrayList<>();
        private GeoBufferInfo<Shorts.Mutable> joints;
        private GeoBufferInfo<Floats.Mutable> weights;
        private final Map<String, GeoBufferInfo<?>> custom = new HashMap<>();

        private Builder(int indexCount, int vertexCount) {
            this.indexCount = Check.positiveOrZero(indexCount, "indexCount");
            this.vertexCount = Check.positiveOrZero(vertexCount, "vertexCount");
        }

        public Builder indices(int offset, int stride, GeoReader<Ints.Mutable> reader) {
            this.indices = new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.SCALAR, ComponentType.UNSIGNED_INT);
            return this;
        }

        public Builder positions(int offset, int stride, GeoReader<Floats.Mutable> reader) {
            this.positions = new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.VECTOR3, ComponentType.FLOAT);
            return this;
        }

        public Builder normals(int offset, int stride, GeoReader<Floats.Mutable> reader) {
            this.normals = new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.VECTOR3, ComponentType.FLOAT);
            return this;
        }

        public Builder tangents(int offset, int stride, GeoReader<Floats.Mutable> reader) {
            this.tangents = new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.VECTOR4, ComponentType.FLOAT);
            return this;
        }

        public Builder addTexCoords(int offset, int stride, GeoReader<Floats.Mutable> reader) {
            this.texCoords.add(new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.VECTOR2, ComponentType.FLOAT));
            return this;
        }

        public Builder addColors(int offset, int stride, GeoReader<Bytes.Mutable> reader) {
            this.colors.add(new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.VECTOR4, ComponentType.UNSIGNED_BYTE));
            return this;
        }

        public Builder joints(int offset, int stride, int length, GeoReader<Shorts.Mutable> reader) {
            this.joints = new GeoBufferInfo<>(offset, stride, length, reader, ElementType.SCALAR, ComponentType.UNSIGNED_SHORT);
            return this;
        }

        public Builder weights(int offset, int stride, int length, GeoReader<Floats.Mutable> reader) {
            this.weights = new GeoBufferInfo<>(offset, stride, length, reader, ElementType.SCALAR, ComponentType.FLOAT);
            return this;
        }

        public <T extends Slice> Builder custom(String name, int offset, int stride, int length, GeoReader<T> reader, ComponentType<T> componentType, ElementType elementType) {
            this.custom.put(name, new GeoBufferInfo<>(offset, stride, length, reader, elementType, componentType));
            return this;
        }

        public GeoMeshInfo build() {
            return new GeoMeshInfo(indexCount, vertexCount, indices, positions, normals, tangents, texCoords, colors, joints, weights, custom);
        }
    }
}
