package be.twofold.valen.core.material;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record MaterialProperty(
    TexturePropertyType type,
    Vector4 factor,
    TextureReference reference
) {
    public MaterialProperty {
        Check.notNull(type, "type");
        if (factor == null && reference == null) {
            throw new NullPointerException("At least one of factor, reference should be provided");
        }
    }
}
