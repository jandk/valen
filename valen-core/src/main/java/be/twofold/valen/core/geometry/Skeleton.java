package be.twofold.valen.core.geometry;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public record Skeleton(
    List<Bone> bones,
    Axis upAxis
) {
    public Skeleton {
        bones = List.copyOf(bones);
        Check.notNull(upAxis, "upAxis");
    }
}
