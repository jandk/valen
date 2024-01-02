package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.util.*;

public record Mesh(
    VertexBuffer faceBuffer,
    Map<Semantic, VertexBuffer> vertexBuffers
) {
    public Mesh {
        Check.notNull(faceBuffer, "faceBuffer must not be null");
        vertexBuffers = Map.copyOf(vertexBuffers);
    }

    public Optional<VertexBuffer> getBuffer(Semantic semantic) {
        return Optional.ofNullable(vertexBuffers.get(semantic));
    }
}
