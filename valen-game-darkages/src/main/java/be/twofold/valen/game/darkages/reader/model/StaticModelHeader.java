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
    public static StaticModelHeader read(BinarySource source) throws IOException {
        Vector3 referencePosition = Vector3.read(source);
        var numLods = source.readInt();
        var numSurfaces = source.readInt();
        var maxLodDeviations = source.readFloats(numLods);
        var streamable = source.readBool(BoolFormat.INT);

        return new StaticModelHeader(
            referencePosition,
            numLods,
            numSurfaces,
            maxLodDeviations,
            streamable
        );
    }
}
