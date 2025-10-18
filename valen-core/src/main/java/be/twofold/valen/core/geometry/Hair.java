package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

public record Hair(
    String name,
    Ints segments,
    Floats positions
) {
    public Hair {
        Check.notNull(name, "name");
        Check.notNull(segments, "segments");
        Check.notNull(positions, "positions");
    }
}
