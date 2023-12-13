package be.twofold.valen.geometry;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record Bounds(
    Vector3 min,
    Vector3 max
) {
    public static Bounds read(BetterBuffer buffer) {
        Vector3 min = Vector3.read(buffer);
        Vector3 max = Vector3.read(buffer);
        return new Bounds(min, max);
    }

    @Override
    public String toString() {
        return "(" + min + ", " + max + ")";
    }
}
