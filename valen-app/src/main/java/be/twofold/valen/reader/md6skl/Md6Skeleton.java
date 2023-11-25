package be.twofold.valen.reader.md6skl;

import java.util.*;

public record Md6Skeleton(
    Md6SkeletonHeader header,
    short[] remapTable,
    List<Md6SkeletonJoint> joints
) {
}
