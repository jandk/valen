package be.twofold.valen.core.geometry;

import be.twofold.valen.core.math.*;

public record Bone(
    String name,
    int parent,
    Quaternion rotation,
    Vector3 scale,
    Vector3 translation,
    Matrix4x4 inverseBasePose
) {
}
