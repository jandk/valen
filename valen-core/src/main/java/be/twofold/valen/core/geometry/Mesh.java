package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.util.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record Mesh(
    Ints indices,
    Floats positions,
    Optional<Floats> normals,
    Optional<Floats> tangents,
    List<Floats> texCoords,
    List<Bytes> colors,
    Optional<Shorts> joints,
    Optional<Floats> weights,
    int maxInfluence,
    Map<String, VertexBuffer<?>> custom,
    Optional<String> name,
    Optional<Material> material,
    List<BlendShape> blendShapes
) {
    public Mesh {
        Check.notNull(indices, "indices");
        Check.argument(positions.length() % 3 == 0, "positions.size() % 3 != 0");

        int vertexCount = positions.length() / 3;
        normals.ifPresent(floats -> check(floats.length(), vertexCount, 3));
        tangents.ifPresent(floats -> check(floats.length(), vertexCount, 4));
        texCoords.forEach(floats -> check(floats.length(), vertexCount, 2));
        colors.forEach(bytes -> check(bytes.length(), vertexCount, 4));
        if (Check.positiveOrZero(maxInfluence, "maxInfluence") > 0) {
            joints.ifPresent(shorts -> check(shorts.length(), vertexCount, maxInfluence));
            weights.ifPresent(floats -> check(floats.length(), vertexCount, maxInfluence));
        }
        custom.values().forEach(vb -> check(vb.array().length(), vertexCount, vb.count()));
    }

    public Mesh(
        Ints indices,
        Floats positions,
        Optional<Floats> normals,
        Optional<Floats> tangents,
        List<Floats> texCoords,
        List<Bytes> colors,
        Optional<Shorts> joints,
        Optional<Floats> weights,
        int maxInfluence,
        Map<String, VertexBuffer<?>> custom
    ) {
        this(indices, positions, normals, tangents, texCoords, colors, joints, weights, maxInfluence, custom, Optional.empty(), Optional.empty(), List.of());
    }

    private static void check(int length, int count, int elementSize) {
        Check.argument(length % elementSize == 0, "array length must be a multiple of elementSize");
        Check.argument(length == count * elementSize, "array length must be equal to count * elementSize");
    }

    public int faceCount() {
        return indices.length() / 3;
    }

    public int vertexCount() {
        return positions.length() / 3;
    }

    public Mesh withJointsAndWeights(Shorts joints, Floats weights) {
        return new Mesh(indices, positions, normals, tangents, texCoords, colors, Optional.of(joints), Optional.of(weights), maxInfluence, custom, name, material, blendShapes);
    }

    public Mesh withMaxInfluence(int maxInfluence) {
        return new Mesh(indices, positions, normals, tangents, texCoords, colors, joints, weights, maxInfluence, custom, name, material, blendShapes);
    }

    public Mesh withName(Optional<String> name) {
        return new Mesh(indices, positions, normals, tangents, texCoords, colors, joints, weights, maxInfluence, custom, name, material, blendShapes);
    }

    public Mesh withMaterial(Optional<Material> material) {
        return new Mesh(indices, positions, normals, tangents, texCoords, colors, joints, weights, maxInfluence, custom, name, material, blendShapes);
    }

    public Mesh withBlendShapes(List<BlendShape> blendShapes) {
        return new Mesh(indices, positions, normals, tangents, texCoords, colors, joints, weights, maxInfluence, custom, name, material, blendShapes);
    }
}
