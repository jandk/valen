package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Leaf(
    int cluster,
    int area,
    Vector3i min,
    Vector3i max,
    int firstLeafSurface,
    int numLeafSurfaces,
    int firstLeafBrush,
    int numLeafBrushes
) {
    public static final int BYTES = 6 * Integer.BYTES + 2 * Vector3i.BYTES;

    public static Leaf read(DataSource source) throws IOException {
        int cluster = source.readInt();
        int area = source.readInt();
        Vector3i min = Vector3i.read(source);
        Vector3i max = Vector3i.read(source);
        int firstLeafSurface = source.readInt();
        int numLeafSurfaces = source.readInt();
        int firstLeafBrush = source.readInt();
        int numLeafBrushes = source.readInt();
        return new Leaf(cluster, area, min, max, firstLeafSurface, numLeafSurfaces, firstLeafBrush, numLeafBrushes);
    }
}
