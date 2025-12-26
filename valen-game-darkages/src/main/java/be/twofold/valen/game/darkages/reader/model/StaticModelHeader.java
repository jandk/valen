package be.twofold.valen.game.darkages.reader.model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;

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
