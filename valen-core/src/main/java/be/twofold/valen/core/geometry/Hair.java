package be.twofold.valen.core.geometry;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

public record Hair(
    String name,
    Ints segments,
    Floats positions
) {
    public Hair {
        Check.nonNull(name, "name");
        Check.nonNull(segments, "segments");
        Check.nonNull(positions, "positions");
    }
}
