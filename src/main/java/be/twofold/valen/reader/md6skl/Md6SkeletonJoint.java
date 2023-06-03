package be.twofold.valen.reader.md6skl;

import be.twofold.valen.geometry.*;

public record Md6SkeletonJoint(
    String name,
    int parent,
    Vector4 rotation,
    Vector3 scale,
    Vector3 translation,
    Mat4 inverseBasePose
) {
}
