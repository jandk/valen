package be.twofold.valen.reader.md6skl;

import be.twofold.valen.geometry.*;

public record Md6SkeletonBone(
    String name,
    int parent,
    Vector4 quat,
    Vector3 pos,
    Vector3 scale
) {
}
