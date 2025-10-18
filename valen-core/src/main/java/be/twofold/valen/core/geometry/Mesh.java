package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public record Mesh(
    IndexBuffer indexBuffer,
    VertexBuffer2 vertexBuffer,
    Optional<String> name,
    Optional<Material> material,
    List<BlendShape> blendShapes
) {
    public Mesh {
        Check.notNull(indexBuffer, "indexBuffer");
    }

    public Mesh(IndexBuffer indexBuffer, VertexBuffer2 vertexBuffer) {
        this(indexBuffer, vertexBuffer, Optional.empty(), Optional.empty(), List.of());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Mesh withName(Optional<String> name) {
        return new Mesh(indexBuffer, vertexBuffer, name, material, blendShapes);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Mesh withMaterial(Optional<Material> material) {
        return new Mesh(indexBuffer, vertexBuffer, name, material, blendShapes);
    }

    public Mesh withBlendShapes(List<BlendShape> blendShapes) {
        return new Mesh(indexBuffer, vertexBuffer, name, material, blendShapes);
    }
}
