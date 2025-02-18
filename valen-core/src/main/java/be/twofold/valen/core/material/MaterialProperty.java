package be.twofold.valen.core.material;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record MaterialProperty(
    MaterialPropertyType type,
    TextureReference reference,
    Vector4 factor
) {
    public MaterialProperty {
        Check.notNull(type, "type");
        if (factor == null && reference == null) {
            throw new NullPointerException("At least one of factor, reference should be provided");
        }
    }

    public MaterialProperty withFactor(Vector4 factor) {
        return new MaterialProperty(type, reference, factor);
    }
}
