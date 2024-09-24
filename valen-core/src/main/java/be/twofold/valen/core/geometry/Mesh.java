package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.util.*;

public record Mesh(
    String name,
    VertexBuffer faceBuffer,
    Map<Semantic, VertexBuffer> vertexBuffers,
    int materialIndex
) {
    public Mesh {
        Check.notNull(faceBuffer, "faceBuffer must not be null");
        vertexBuffers = Map.copyOf(vertexBuffers);
    }

    public Mesh(VertexBuffer faceBuffer, Map<Semantic, VertexBuffer> vertexBuffers, int materialIndex) {
        this("Mesh", faceBuffer, vertexBuffers, materialIndex);
    }

    public Optional<VertexBuffer> getBuffer(Semantic semantic) {
        return Optional.ofNullable(vertexBuffers.get(semantic));
    }

    public Mesh withMaterialIndex(int materialIndex) {
        return new Mesh(faceBuffer, vertexBuffers, materialIndex);
    }
}
