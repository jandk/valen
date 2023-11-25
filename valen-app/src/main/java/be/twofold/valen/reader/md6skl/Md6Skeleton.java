package be.twofold.valen.reader.md6skl;

import be.twofold.valen.core.geometry.*;

import java.util.*;

public record Md6Skeleton(
    Md6SkeletonHeader header,
    short[] remapTable,
    List<Bone> joints
) {
}
