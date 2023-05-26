package be.twofold.valen.reader.md6;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;

public record Md6Header(
    String sklName,
    Vector3 min,
    Vector3 max,
    boolean regular
) {
    public static Md6Header read(BetterBuffer buffer) {
        String sklName = buffer.getString();
        Vector3 min = buffer.getVector3();
        Vector3 max = buffer.getVector3();
        boolean regular = buffer.getByteAsBool(); // true for md6skel, false for alembic
        buffer.expectInt(0);

        return new Md6Header(sklName, min, max, regular);
    }
}
