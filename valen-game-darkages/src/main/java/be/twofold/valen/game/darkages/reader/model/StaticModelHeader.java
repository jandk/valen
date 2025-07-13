package be.twofold.valen.game.darkages.reader.model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record StaticModelHeader(
    Vector3 referencePosition,
    int numLods,
    int numSurfaces,
    float[] maxLodDeviations,
    boolean streamable
) {
    public static StaticModelHeader read(BinaryReader reader) throws IOException {
        Vector3 referencePosition = Vector3.read(reader);
        var numLods = reader.readInt();
        var numSurfaces = reader.readInt();
        var maxLodDeviations = reader.readFloats(numLods);
        var streamable = reader.readBoolInt();

        return new StaticModelHeader(
            referencePosition,
            numLods,
            numSurfaces,
            maxLodDeviations,
            streamable
        );
    }
}
