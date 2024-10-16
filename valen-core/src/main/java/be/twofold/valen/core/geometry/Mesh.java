package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public record Mesh(
    String name,
    VertexBuffer faceBuffer,
    Map<Semantic, VertexBuffer> vertexBuffers,
    Material material
) {
    public Mesh {
        Check.notNull(faceBuffer, "faceBuffer must not be null");
        vertexBuffers = Map.copyOf(vertexBuffers);
    }

    public Optional<VertexBuffer> getBuffer(Semantic semantic) {
        return Optional.ofNullable(vertexBuffers.get(semantic));
    }

    public Mesh withName(String name) {
        return new Mesh(name, faceBuffer, vertexBuffers, material);
    }

    public Mesh withMaterial(Material material) {
        return new Mesh(name, faceBuffer, vertexBuffers, material);
    }
}
