package be.twofold.valen.core.material;

import wtf.reversed.toolbox.math.*;
import wtf.reversed.toolbox.util.*;

public record MaterialProperty(
    MaterialPropertyType type,
    TextureReference reference,
    Vector4 factor
) {
    public MaterialProperty {
        Check.nonNull(type, "type");
        Check.argument(factor != null || reference != null,
            "At least one of factor, reference should be provided");
    }

    public MaterialProperty withFactor(Vector4 factor) {
        return new MaterialProperty(type, reference, factor);
    }
}
