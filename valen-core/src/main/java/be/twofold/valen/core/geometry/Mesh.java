package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public record Mesh(
    VertexBuffer<?> indexBuffer,
    List<VertexBuffer<?>> vertexBuffers,
    Optional<Material> material,
    Optional<String> name,
    List<BlendShape> blendShapes
) {
    public Mesh {
        Check.notNull(indexBuffer, "indices must not be null");
        vertexBuffers = List.copyOf(vertexBuffers);
    }

    public Mesh(VertexBuffer<?> indexBuffer, List<VertexBuffer<?>> vertexBuffers) {
        this(indexBuffer, vertexBuffers, Optional.empty(), Optional.empty(), List.of());
    }

    public Optional<VertexBuffer<?>> getBuffer(Semantic semantic) {
        return getBuffers(semantic).stream().findFirst();
    }

    public List<VertexBuffer<?>> getBuffers(Semantic semantic) {
        return vertexBuffers.stream()
            .filter(vb -> vb.info().semantic() == semantic)
            .toList();
    }

    public Mesh withVertexBuffers(List<VertexBuffer<?>> vertexBuffers) {
        return new Mesh(indexBuffer, vertexBuffers, material, name, blendShapes);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Mesh withMaterial(Optional<Material> material) {
        return new Mesh(indexBuffer, vertexBuffers, material, name, blendShapes);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Mesh withName(Optional<String> name) {
        return new Mesh(indexBuffer, vertexBuffers, material, name, blendShapes);
    }

    public Mesh withBlendShapes(List<BlendShape> blendShapes) {
        return new Mesh(indexBuffer, vertexBuffers, material, name, blendShapes);
    }
}
