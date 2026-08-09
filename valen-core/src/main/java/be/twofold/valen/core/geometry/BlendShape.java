package be.twofold.valen.core.geometry;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

/**
 * A set of vertex displacements, stored sparsely: {@code indices} names the vertices that move
 * and {@code values} holds three floats of displacement for each of them.
 * <p>
 * Slices rather than NIO buffers on purpose. A buffer carries a cursor that writing it out
 * consumes, which made a blend shape measure as empty the second time anything looked at it.
 */
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
