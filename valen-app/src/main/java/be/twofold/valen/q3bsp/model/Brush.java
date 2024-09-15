package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Brush(
    int firstSide,
    int numSides,
    int shaderNum
) {
    public static final int BYTES = 3 * Integer.BYTES;

    public static Brush read(DataSource source) throws IOException {
        int firstSide = source.readInt();
        int numSides = source.readInt();
        int shaderNum = source.readInt();
        return new Brush(firstSide, numSides, shaderNum);
    }
}
