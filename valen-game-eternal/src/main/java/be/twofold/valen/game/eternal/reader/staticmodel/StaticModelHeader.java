package be.twofold.valen.game.eternal.reader.staticmodel;

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
    public static StaticModelHeader read(DataSource source) throws IOException {
        Vector3 referencePosition = source.readVector3();
        var numLods = source.readInt();
        var numSurfaces = source.readInt();
        var maxLodDeviations = source.readFloats(numLods);
        var streamable = source.readBoolInt();

        return new StaticModelHeader(
            referencePosition,
            numLods,
            numSurfaces,
            maxLodDeviations,
            streamable
        );
    }
}
