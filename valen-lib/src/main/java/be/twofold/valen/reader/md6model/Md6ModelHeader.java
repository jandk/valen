package be.twofold.valen.reader.md6model;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record Md6ModelHeader(
    String skelName,
    Bounds bounds,
    boolean regular
) {
    public static Md6ModelHeader read(BetterBuffer buffer) {
        var skelName = buffer.getString();
        var bounds = Bounds.read(buffer);
        var regular = buffer.getByteAsBool(); // true for md6skel, false for alembic
        buffer.expectInt(0);

        return new Md6ModelHeader(skelName, bounds, regular);
    }
}
