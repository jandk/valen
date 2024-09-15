package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Model(
    Vector3 min,
    Vector3 max,
    int firstSurface,
    int numSurfaces,
    int firstBrush,
    int numBrushes
) {
    public static int BYTES = 2 * Vector3.BYTES + 4 * Integer.BYTES;

    public static Model read(DataSource source) throws IOException {
        Vector3 min = Vector3.read(source);
        Vector3 max = Vector3.read(source);
        int firstSurface = source.readInt();
        int numSurfaces = source.readInt();
        int firstBrush = source.readInt();
        int numBrushes = source.readInt();
        return new Model(min, max, firstSurface, numSurfaces, firstBrush, numBrushes);
    }
}
