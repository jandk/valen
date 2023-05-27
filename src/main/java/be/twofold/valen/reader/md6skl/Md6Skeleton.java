package be.twofold.valen.reader.md6skl;

import java.util.*;

public record Md6Skeleton(
    Md6SkeletonHeader header,
    List<Md6SkeletonBone> bones
) {
}
