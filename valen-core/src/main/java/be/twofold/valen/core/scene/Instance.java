package be.twofold.valen.core.scene;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import wtf.reversed.toolbox.util.*;

public record Instance(
    ModelReference modelReference,
    Vector3 translation,
    Quaternion rotation,
    Vector3 scale,
    String name
) {
    public Instance {
        Check.nonNull(modelReference, "modelReference");
        Check.nonNull(translation, "translation");
        Check.nonNull(rotation, "rotation");
        Check.nonNull(scale, "scale");
    }
}
