package be.twofold.valen.game.eternal.reader.staticmodel;

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
    public static StaticModelHeader read(BinaryReader reader) throws IOException {
        Vector3 referencePosition = Vector3.read(reader);
        var numLods = reader.readInt();
        var numSurfaces = reader.readInt();
        var maxLodDeviations = reader.readFloatsStruct(numLods);
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
