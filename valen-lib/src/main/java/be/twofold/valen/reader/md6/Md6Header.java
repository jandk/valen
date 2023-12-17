package be.twofold.valen.reader.md6;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record Md6Header(
    String sklName,
    Vector3 min,
    Vector3 max,
    boolean regular
) {
    public static Md6Header read(BetterBuffer buffer) {
        String sklName = buffer.getString();
        Vector3 min = Vector3.read(buffer);
        Vector3 max = Vector3.read(buffer);
        boolean regular = buffer.getByteAsBool(); // true for md6skel, false for alembic
        buffer.expectInt(0);

        return new Md6Header(sklName, min, max, regular);
    }
}
