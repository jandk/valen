package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.nio.*;

public record BlendShape(
    String name,
    FloatBuffer values,
    ShortBuffer indices
) {
    public BlendShape {
        Check.nonNull(name, "name");
        Check.nonNull(values, "values");
        Check.nonNull(indices, "indices");
    }
}
