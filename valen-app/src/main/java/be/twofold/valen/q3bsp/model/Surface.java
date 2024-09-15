package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Surface(
    int shaderNum,
    int fogNum,
    int surfaceType,
    int firstVert,
    int numVerts,
    int firstIndex,
    int numIndexes,
    int lightmapNum,
    int lightmapX,
    int lightmapY,
    int lightmapWidth,
    int lightmapHeight,
    Vector3 lightmapOrigin,
    Vector3[] lightmapVecs,
    int patchWidth,
    int patchHeight
) {
    public static final int BYTES = 14 * Integer.BYTES + 4 * Vector3.BYTES;

    public static Surface read(DataSource source) throws IOException {
        int shaderNum = source.readInt();
        int fogNum = source.readInt();
        int surfaceType = source.readInt();
        int firstVert = source.readInt();
        int numVerts = source.readInt();
        int firstIndex = source.readInt();
        int numIndexes = source.readInt();
        int lightmapNum = source.readInt();
        int lightmapX = source.readInt();
        int lightmapY = source.readInt();
        int lightmapWidth = source.readInt();
        int lightmapHeight = source.readInt();
        Vector3 lightmapOrigin = Vector3.read(source);
        Vector3[] lightmapVecs = new Vector3[3];
        for (int i = 0; i < 3; i++) {
            lightmapVecs[i] = Vector3.read(source);
        }
        int patchWidth = source.readInt();
        int patchHeight = source.readInt();
        return new Surface(
            shaderNum,
            fogNum,
            surfaceType,
            firstVert,
            numVerts,
            firstIndex,
            numIndexes,
            lightmapNum,
            lightmapX,
            lightmapY,
            lightmapWidth,
            lightmapHeight,
            lightmapOrigin,
            lightmapVecs,
            patchWidth,
            patchHeight
        );
    }
}
