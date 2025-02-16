package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public record Mesh(
    VertexBuffer indexBuffer,
    Map<Semantic, VertexBuffer> vertexBuffers,
    Material material,
    String name
) {
    public Mesh {
        Check.notNull(indexBuffer, "indexBuffer must not be null");
        vertexBuffers = Map.copyOf(vertexBuffers);
    }

    public Mesh(
        VertexBuffer indexBuffer,
        Map<Semantic, VertexBuffer> vertexBuffers
    ) {
        this(indexBuffer, vertexBuffers, null, null);
    }

    public Optional<VertexBuffer> getBuffer(Semantic semantic) {
        return Optional.ofNullable(vertexBuffers.get(semantic));
    }

    public Optional<Material> materialOpt() {
        return Optional.ofNullable(material);
    }

    public Optional<String> nameOpt() {
        return Optional.ofNullable(name);
    }

    public Mesh withMaterial(Material material) {
        return new Mesh(indexBuffer, vertexBuffers, material, name);
    }

    public Mesh withName(String name) {
        return new Mesh(indexBuffer, vertexBuffers, material, name);
    }
}
