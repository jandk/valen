package be.twofold.valen.core.geometry;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

public record BlendShape(
    String name,
    Floats values,
    Shorts indices
) {
    public BlendShape {
        Check.nonNull(name, "name");
        Check.nonNull(values, "values");
        Check.nonNull(indices, "indices");
    }
}
