package be.twofold.valen.game.darkages.reader.model;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

public record StaticModelHeader(
    Vector3 referencePosition,
    int numLods,
    int numSurfaces,
    Floats maxLodDeviations,
    boolean streamable
) {
    public static StaticModelHeader read(BinaryReader reader) throws IOException {
        Vector3 referencePosition = Vector3.read(reader);
        var numLods = reader.readInt();
        var numSurfaces = reader.readInt();
        var maxLodDeviations = reader.readFloats(numLods);
        var streamable = reader.readBool(BoolFormat.INT);

        return new StaticModelHeader(
            referencePosition,
            numLods,
            numSurfaces,
            maxLodDeviations,
            streamable
        );
    }
}
