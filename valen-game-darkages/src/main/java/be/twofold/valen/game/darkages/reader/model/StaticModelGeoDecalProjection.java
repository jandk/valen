package be.twofold.valen.game.darkages.reader.model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record StaticModelGeoDecalProjection(
    Quaternion projS,
    Quaternion projT,
    Quaternion projR
) {
    public static StaticModelGeoDecalProjection read(BinaryReader reader) throws IOException {
        var projS = Quaternion.read(reader);
        var projT = Quaternion.read(reader);
        var projR = Quaternion.read(reader);
        return new StaticModelGeoDecalProjection(projS, projT, projR);
    }
}
