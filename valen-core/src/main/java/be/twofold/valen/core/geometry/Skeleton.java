package be.twofold.valen.core.geometry;

import java.util.*;

public record Skeleton(
    List<Bone> bones
) {
    public Skeleton {
        bones = List.copyOf(bones);
    }
}
