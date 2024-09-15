package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;

import java.io.*;

public record BrushSide(
    int planeNum,
    int shaderNum
) {
    public static final int BYTES = 2 * Integer.BYTES;

    public static BrushSide read(DataSource source) throws IOException {
        int planeNum = source.readInt();
        int shaderNum = source.readInt();
        return new BrushSide(planeNum, shaderNum);
    }
}
