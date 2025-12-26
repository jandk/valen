package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

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
