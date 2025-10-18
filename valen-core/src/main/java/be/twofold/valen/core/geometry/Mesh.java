package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.util.*;

public record Mesh(
    Ints indexBuffer,
    List<VertexBuffer<?>> vertexBuffers,
    Optional<String> name,
    Optional<Material> material,
    List<BlendShape> blendShapes
) {
    public Mesh {
        Check.notNull(indexBuffer, "indexBuffer");
        vertexBuffers = List.copyOf(vertexBuffers);
    }

    public Mesh(Ints indexBuffer, List<VertexBuffer<?>> vertexBuffers) {
        this(indexBuffer, vertexBuffers, Optional.empty(), Optional.empty(), List.of());
    }

    public Optional<VertexBuffer<?>> getBuffer(Semantic semantic) {
        return getBuffers(semantic).stream().findFirst();
    }

    public VertexBuffer<Floats> getPositions() {
        return getBuffer(Semantic.POSITION).map(vb -> (VertexBuffer<Floats>) vb).orElseThrow();
    }

    public Optional<VertexBuffer<Floats>> getNormals() {
        return getBuffer(Semantic.NORMAL).map(vb -> (VertexBuffer<Floats>) vb);
    }

    public Optional<VertexBuffer<Floats>> getTangents() {
        return getBuffer(Semantic.TANGENT).map(vb -> (VertexBuffer<Floats>) vb);
    }

    public Optional<VertexBuffer<Floats>> getTexCoords() {
        return getBuffer(Semantic.TEX_COORD).map(vb -> (VertexBuffer<Floats>) vb);
    }

    public Optional<VertexBuffer<Shorts>> getJoints() {
        return getBuffer(Semantic.JOINTS).map(vb -> (VertexBuffer<Shorts>) vb);
    }

    public Optional<VertexBuffer<Floats>> getWeights() {
        return getBuffer(Semantic.WEIGHTS).map(vb -> (VertexBuffer<Floats>) vb);
    }

    public List<VertexBuffer<?>> getBuffers(Semantic semantic) {
        return vertexBuffers.stream()
            .filter(vb -> vb.info().semantic() == semantic)
            .toList();
    }

    public Mesh withVertexBuffers(List<VertexBuffer<?>> vertexBuffers) {
        return new Mesh(indexBuffer, vertexBuffers, name, material, blendShapes);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Mesh withMaterial(Optional<Material> material) {
        return new Mesh(indexBuffer, vertexBuffers, name, material, blendShapes);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Mesh withName(Optional<String> name) {
        return new Mesh(indexBuffer, vertexBuffers, name, material, blendShapes);
    }

    public Mesh withBlendShapes(List<BlendShape> blendShapes) {
        return new Mesh(indexBuffer, vertexBuffers, name, material, blendShapes);
    }
}
