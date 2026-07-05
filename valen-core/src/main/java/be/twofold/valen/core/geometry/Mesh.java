package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record Mesh(
    Ints indices,
    int vertexCount,
    Map<Semantic, VertexBuffer<?>> attributes,
    Optional<String> name,
    Optional<Material> material,
    List<BlendShape> blendShapes
) {
    public Mesh {
        Check.nonNull(indices, "indices");
        Check.positiveOrZero(vertexCount, "vertexCount");
        attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        blendShapes = List.copyOf(blendShapes);
        attributes.forEach((semantic, buffer) -> Check.argument(
            buffer.array().length() == vertexCount * buffer.count(),
            "attribute " + semantic + " has wrong length"));
    }

    public static Builder builder(Ints indices, int vertexCount) {
        return new Builder(indices, vertexCount);
    }

    public Builder toBuilder() {
        return new Builder(indices, vertexCount)
            .attributes(attributes)
            .name(name.orElse(null))
            .material(material.orElse(null))
            .blendShapes(blendShapes);
    }

    public int faceCount() {
        return indices.length() / 3;
    }

    public int maxInfluence() {
        VertexBuffer<?> buffer = attributes.get(Semantic.JOINTS);
        if (buffer == null) {
            buffer = attributes.get(Semantic.WEIGHTS);
        }
        return buffer == null ? 0 : buffer.count();
    }

    public Floats positions() {
        return attribute(Semantic.POSITION, Floats.class).orElseThrow();
    }

    public Optional<Floats> normals() {
        return attribute(Semantic.NORMAL, Floats.class);
    }

    public Optional<Floats> tangents() {
        return attribute(Semantic.TANGENT, Floats.class);
    }

    public List<Floats> texCoords() {
        return attributes.entrySet().stream()
            .filter(e -> e.getKey() instanceof Semantic.TexCoord)
            .sorted(Comparator.comparingInt(e -> ((Semantic.TexCoord) e.getKey()).set()))
            .map(e -> (Floats) e.getValue().array())
            .toList();
    }

    public List<Bytes> colors() {
        return attributes.entrySet().stream()
            .filter(e -> e.getKey() instanceof Semantic.Color)
            .sorted(Comparator.comparingInt(e -> ((Semantic.Color) e.getKey()).set()))
            .map(e -> (Bytes) e.getValue().array())
            .toList();
    }

    public Optional<Shorts> joints() {
        return attribute(Semantic.JOINTS, Shorts.class);
    }

    public Optional<Floats> weights() {
        return attribute(Semantic.WEIGHTS, Floats.class);
    }

    public Map<String, VertexBuffer<?>> custom() {
        var result = new LinkedHashMap<String, VertexBuffer<?>>();
        attributes.forEach((semantic, buffer) -> {
            if (semantic instanceof Semantic.Custom custom) {
                result.put(custom.name(), buffer);
            }
        });
        return Collections.unmodifiableMap(result);
    }

    private <T extends Slice> Optional<T> attribute(Semantic semantic, Class<T> type) {
        VertexBuffer<?> buffer = attributes.get(semantic);
        return buffer == null ? Optional.empty() : Optional.of(type.cast(buffer.array()));
    }

    public static final class Builder {
        private final Ints indices;
        private final int vertexCount;
        private final Map<Semantic, VertexBuffer<?>> attributes = new LinkedHashMap<>();
        private int texCoordCount = 0;
        private int colorCount = 0;
        private Optional<String> name = Optional.empty();
        private Optional<Material> material = Optional.empty();
        private List<BlendShape> blendShapes = List.of();

        private Builder(Ints indices, int vertexCount) {
            this.indices = Check.nonNull(indices, "indices");
            this.vertexCount = Check.positiveOrZero(vertexCount, "vertexCount");
        }

        public Builder attribute(Semantic semantic, VertexBuffer<?> buffer) {
            attributes.put(semantic, buffer);
            return this;
        }

        public Builder position(Floats positions) {
            return attribute(Semantic.POSITION, buffer(positions, 1, ElementType.VECTOR3, ComponentType.FLOAT));
        }

        public Builder normal(Floats normals) {
            return attribute(Semantic.NORMAL, buffer(normals, 1, ElementType.VECTOR3, ComponentType.FLOAT));
        }

        public Builder tangent(Floats tangents) {
            return attribute(Semantic.TANGENT, buffer(tangents, 1, ElementType.VECTOR4, ComponentType.FLOAT));
        }

        public Builder addTexCoord(Floats texCoord) {
            return attribute(new Semantic.TexCoord(texCoordCount++), buffer(texCoord, 1, ElementType.VECTOR2, ComponentType.FLOAT));
        }

        public Builder addColor(Bytes color) {
            return attribute(new Semantic.Color(colorCount++), buffer(color, 1, ElementType.VECTOR4, ComponentType.UNSIGNED_BYTE));
        }

        public Builder joints(Shorts joints) {
            return attribute(Semantic.JOINTS, buffer(joints, joints.length() / vertexCount, ElementType.SCALAR, ComponentType.UNSIGNED_SHORT));
        }

        public Builder weights(Floats weights) {
            return attribute(Semantic.WEIGHTS, buffer(weights, weights.length() / vertexCount, ElementType.SCALAR, ComponentType.FLOAT));
        }

        public Builder custom(String name, VertexBuffer<?> buffer) {
            return attribute(new Semantic.Custom(name), buffer);
        }

        public Builder name(String name) {
            this.name = Optional.ofNullable(name);
            return this;
        }

        public Builder material(Material material) {
            this.material = Optional.ofNullable(material);
            return this;
        }

        public Builder blendShapes(List<BlendShape> blendShapes) {
            this.blendShapes = List.copyOf(blendShapes);
            return this;
        }

        public Mesh build() {
            return new Mesh(indices, vertexCount, attributes, name, material, blendShapes);
        }

        private Builder attributes(Map<Semantic, VertexBuffer<?>> attributes) {
            this.attributes.putAll(attributes);
            this.texCoordCount = (int) attributes.keySet().stream().filter(s -> s instanceof Semantic.TexCoord).count();
            this.colorCount = (int) attributes.keySet().stream().filter(s -> s instanceof Semantic.Color).count();
            return this;
        }

        private static <T extends Slice> VertexBuffer<T> buffer(Slice array, int length, ElementType elementType, ComponentType<T> componentType) {
            @SuppressWarnings("unchecked")
            T typed = (T) array;
            return new VertexBuffer<>(typed, new AttributeLayout<>(length, elementType, componentType));
        }
    }
}
