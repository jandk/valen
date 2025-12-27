package be.twofold.valen.core.geometry;

import be.twofold.valen.core.math.*;
import wtf.reversed.toolbox.util.*;

public record Bone(
    String name,
    int parent,
    Quaternion rotation,
    Vector3 scale,
    Vector3 translation,
    Matrix4 inverseBasePose
) {
    public Bone {
        Check.nonNull(name, "name");
        Check.nonNull(rotation, "rotation");
        Check.nonNull(scale, "scale");
        Check.nonNull(translation, "translation");
        Check.nonNull(inverseBasePose, "inverseBasePose");
    }
}
