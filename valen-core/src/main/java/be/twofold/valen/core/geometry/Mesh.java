package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public record Mesh(
    VertexBuffer<?> indexBuffer,
    List<VertexBuffer<?>> vertexBuffers,
    Material material,
    String name
) {
    public Mesh {
        Check.notNull(indexBuffer, "indexBuffer must not be null");

        var count = vertexBuffers.stream()
            .map(vb -> vb.info().semantic())
            .distinct().count();
        if (vertexBuffers.size() != count) {
            throw new IllegalArgumentException("Multiple buffers with the same semantic");
        }
        vertexBuffers = List.copyOf(vertexBuffers);
    }

    public Mesh(VertexBuffer<?> indexBuffer, List<VertexBuffer<?>> vertexBuffers) {
        this(indexBuffer, vertexBuffers, null, null);
    }

    public Optional<VertexBuffer<?>> getBuffer(Semantic semantic) {
        return vertexBuffers.stream()
            .filter(vb -> vb.info().semantic().equals(semantic))
            .findFirst();
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
