package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Node(
    int plane,
    int lo,
    int hi,
    Vector3i mins,
    Vector3i maxs
) {
    public static final int BYTES = 3 * Integer.BYTES + 2 * Vector3i.BYTES;

    public static Node read(DataSource source) throws IOException {
        int plane = source.readInt();
        int lo = source.readInt();
        int hi = source.readInt();
        Vector3i mins = Vector3i.read(source);
        Vector3i maxs = Vector3i.read(source);
        return new Node(plane, lo, hi, mins, maxs);
    }
}
