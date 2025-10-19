package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.util.*;

public final class GeoMeshInfo {
    private final int indexCount;
    private final int vertexCount;
    private final GeoBufferInfo<MutableInts> indices;
    private final GeoBufferInfo<MutableFloats> positions;
    private final GeoBufferInfo<MutableFloats> normals;
    private final GeoBufferInfo<MutableFloats> tangents;
    private final List<GeoBufferInfo<MutableFloats>> texCoords;
    private final List<GeoBufferInfo<MutableBytes>> colors;
    private final GeoBufferInfo<MutableShorts> joints;
    private final GeoBufferInfo<MutableFloats> weights;
    private final Map<String, GeoBufferInfo<?>> custom;

    public GeoMeshInfo(
        int indexCount,
        int vertexCount,
        GeoBufferInfo<MutableInts> indices,
        GeoBufferInfo<MutableFloats> positions,
        GeoBufferInfo<MutableFloats> normals,
        GeoBufferInfo<MutableFloats> tangents,
        List<GeoBufferInfo<MutableFloats>> texCoords,
        List<GeoBufferInfo<MutableBytes>> colors,
        GeoBufferInfo<MutableShorts> joints,
        GeoBufferInfo<MutableFloats> weights,
        Map<String, GeoBufferInfo<?>> custom
    ) {
        this.indexCount = indexCount;
        this.vertexCount = vertexCount;
        this.indices = Check.notNull(indices, "indices");
        this.positions = Check.notNull(positions, "positions");
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

    public GeoBufferInfo<MutableInts> indices() {
        return indices;
    }

    public int vertexCount() {
        return vertexCount;
    }

    public GeoBufferInfo<MutableFloats> positions() {
        return positions;
    }

    public Optional<GeoBufferInfo<MutableFloats>> normals() {
        return Optional.ofNullable(normals);
    }

    public Optional<GeoBufferInfo<MutableFloats>> tangents() {
        return Optional.ofNullable(tangents);
    }

    public List<GeoBufferInfo<MutableFloats>> texCoords() {
        return texCoords;
    }

    public List<GeoBufferInfo<MutableBytes>> colors() {
        return colors;
    }

    public Optional<GeoBufferInfo<MutableShorts>> joints() {
        return Optional.ofNullable(joints);
    }

    public Optional<GeoBufferInfo<MutableFloats>> weights() {
        return Optional.ofNullable(weights);
    }

    public Map<String, GeoBufferInfo<?>> custom() {
        return custom;
    }

    public static final class Builder {
        private final int indexCount;
        private final int vertexCount;

        private GeoBufferInfo<MutableInts> indices;
        private GeoBufferInfo<MutableFloats> positions;
        private GeoBufferInfo<MutableFloats> normals;
        private GeoBufferInfo<MutableFloats> tangents;
        private final List<GeoBufferInfo<MutableFloats>> texCoords = new ArrayList<>();
        private final List<GeoBufferInfo<MutableBytes>> colors = new ArrayList<>();
        private GeoBufferInfo<MutableShorts> joints;
        private GeoBufferInfo<MutableFloats> weights;
        private final Map<String, GeoBufferInfo<?>> custom = new HashMap<>();

        private Builder(int indexCount, int vertexCount) {
            this.indexCount = Check.positiveOrZero(indexCount, "indexCount");
            this.vertexCount = Check.positiveOrZero(vertexCount, "vertexCount");
        }

        public Builder indices(int offset, int stride, GeoReader<MutableInts> reader) {
            this.indices = new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.SCALAR, ComponentType.UNSIGNED_INT);
            return this;
        }

        public Builder positions(int offset, int stride, GeoReader<MutableFloats> reader) {
            this.positions = new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.VECTOR3, ComponentType.FLOAT);
            return this;
        }

        public Builder normals(int offset, int stride, GeoReader<MutableFloats> reader) {
            this.normals = new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.VECTOR3, ComponentType.FLOAT);
            return this;
        }

        public Builder tangents(int offset, int stride, GeoReader<MutableFloats> reader) {
            this.tangents = new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.VECTOR4, ComponentType.FLOAT);
            return this;
        }

        public Builder addTexCoords(int offset, int stride, GeoReader<MutableFloats> reader) {
            this.texCoords.add(new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.VECTOR2, ComponentType.FLOAT));
            return this;
        }

        public Builder addColors(int offset, int stride, GeoReader<MutableBytes> reader) {
            this.colors.add(new GeoBufferInfo<>(offset, stride, 1, reader, ElementType.VECTOR4, ComponentType.UNSIGNED_BYTE));
            return this;
        }

        public Builder joints(int offset, int stride, int length, GeoReader<MutableShorts> reader) {
            this.joints = new GeoBufferInfo<>(offset, stride, length, reader, ElementType.SCALAR, ComponentType.UNSIGNED_SHORT);
            return this;
        }

        public Builder weights(int offset, int stride, int length, GeoReader<MutableFloats> reader) {
            this.weights = new GeoBufferInfo<>(offset, stride, length, reader, ElementType.SCALAR, ComponentType.FLOAT);
            return this;
        }

        public <T extends WrappedArray> Builder custom(String name, int offset, int stride, GeoReader<T> reader, ComponentType<T> componentType, ElementType elementType) {
            this.custom.put(name, new GeoBufferInfo<>(offset, stride, 1, reader, elementType, componentType));
            return this;
        }

        public GeoMeshInfo build() {
            return new GeoMeshInfo(indexCount, vertexCount, indices, positions, normals, tangents, texCoords, colors, joints, weights, custom);
        }
    }
}
