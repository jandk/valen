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

    public Optional<VertexBuffer> getBuffer(Semantic semantic) {
        return Optional.ofNullable(vertexBuffers.get(semantic));
    }

    public Mesh withMaterialIndex(int materialIndex) {
        return new Mesh(name, faceBuffer, vertexBuffers, materialIndex);
    }

    public Mesh withName(String name) {
        return new Mesh(name, faceBuffer, vertexBuffers, materialIndex);
    }
}
