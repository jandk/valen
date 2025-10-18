package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.util.*;

public record Mesh(
    Ints indexBuffer,
    Floats positions,
    Optional<Floats> normals,
    Optional<Floats> tangents,
    List<Floats> texCoords,
    List<VertexBuffer<?>> vertexBuffers,
    Optional<String> name,
    Optional<Material> material,
    List<BlendShape> blendShapes
) {
    public Mesh {
        Check.notNull(indexBuffer, "indexBuffer");
        vertexBuffers = List.copyOf(vertexBuffers);
    }

    public Mesh(Ints indexBuffer, Floats positions, Optional<Floats> normals, Optional<Floats> tangents, List<Floats> texCoords, List<VertexBuffer<?>> vertexBuffers) {
        this(indexBuffer, positions, normals, tangents, texCoords, vertexBuffers, Optional.empty(), Optional.empty(), List.of());
    }

    public int getNumTriangles() {
        return indexBuffer.size() / 3;
    }

    public int getNumVertices() {
        return positions.size() / 3;
    }

    public Floats getPositions() {
        return positions;
    }

    public Optional<Floats> getNormals() {
        return normals;
    }

    public Optional<Floats> getTangents() {
        return tangents;
    }

    public List<Floats> getTexCoords() {
        return texCoords;
    }

    public Optional<VertexBuffer<Shorts>> getJoints() {
        return getBuffer(Semantic.JOINTS).map(vb -> (VertexBuffer<Shorts>) vb);
    }

    public Optional<VertexBuffer<Floats>> getWeights() {
        return getBuffer(Semantic.WEIGHTS).map(vb -> (VertexBuffer<Floats>) vb);
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
        return new Mesh(indexBuffer, positions, normals, tangents, texCoords, vertexBuffers, name, material, blendShapes);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Mesh withMaterial(Optional<Material> material) {
        return new Mesh(indexBuffer, positions, normals, tangents, texCoords, vertexBuffers, name, material, blendShapes);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Mesh withName(Optional<String> name) {
        return new Mesh(indexBuffer, positions, normals, tangents, texCoords, vertexBuffers, name, material, blendShapes);
    }

    public Mesh withBlendShapes(List<BlendShape> blendShapes) {
        return new Mesh(indexBuffer, positions, normals, tangents, texCoords, vertexBuffers, name, material, blendShapes);
    }
}
