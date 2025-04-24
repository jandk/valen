package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.nio.*;

public record BlendShape(
    String name,
    FloatBuffer values,
    ShortBuffer indices
) {
    public BlendShape {
        Check.notNull(name, "name");
        Check.notNull(values, "values");
        Check.notNull(indices, "indices");
    }
}
