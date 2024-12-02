package be.twofold.valen.core.scene;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record Instance(
    String name,
    ModelReference modelReference,
    Vector3 translation,
    Quaternion rotation,
    Vector3 scale
) {
    public Instance {
        Check.notNull(modelReference, "modelReference");
        Check.notNull(translation, "translation");
        Check.notNull(rotation, "rotation");
        Check.notNull(scale, "scale");
    }
}
