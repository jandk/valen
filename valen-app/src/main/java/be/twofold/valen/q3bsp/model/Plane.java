package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Plane(
    Vector3 normal,
    float distance
) {
    public static final int BYTES = Vector3.BYTES + Float.BYTES;

    public static Plane read(DataSource source) throws IOException {
        Vector3 normal = Vector3.read(source);
        float distance = source.readFloat();
        return new Plane(normal, distance);
    }
}
