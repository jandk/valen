package be.twofold.valen.writer.gltf.model;

import be.twofold.valen.core.math.*;

import java.util.*;

public record NodeSchema(
    String name,
    Quaternion rotation,
    Vector3 translation,
    Vector3 scale,
    List<Integer> children,
    Integer mesh,
    Integer skin
) {
    public static NodeSchema buildSkeletonNode(String name, Quaternion rotation, Vector3 translation, Vector3 scale, List<Integer> children) {
        return new NodeSchema(name, rotation, translation, scale, children, null, null);
    }

    public static NodeSchema buildMeshSkin(int mesh, Integer skin) {
        return new NodeSchema(null, null, null, null, null, mesh, skin);
    }
}
