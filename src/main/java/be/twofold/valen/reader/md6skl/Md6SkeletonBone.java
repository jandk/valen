package be.twofold.valen.reader.md6skl;

import be.twofold.valen.geometry.*;

public record Md6SkeletonBone(
    String name,
    int parent,
    Vector4 rotation,
    Vector3 translation,
    Vector3 scale
) {
}
