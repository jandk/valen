package be.twofold.valen.core.geometry;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record Bone(
    String name,
    int parent,
    Quaternion rotation,
    Vector3 scale,
    Vector3 translation,
    Matrix4 inverseBasePose
) {
    public Bone {
        Check.notNull(name, "name");
        Check.notNull(rotation, "rotation");
        Check.notNull(scale, "scale");
        Check.notNull(translation, "translation");
        Check.notNull(inverseBasePose, "inverseBasePose");
    }
}
