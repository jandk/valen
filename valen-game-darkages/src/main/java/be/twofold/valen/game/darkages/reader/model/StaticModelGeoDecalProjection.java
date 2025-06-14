package be.twofold.valen.game.darkages.reader.model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record StaticModelGeoDecalProjection(
    Quaternion projS,
    Quaternion projT,
    Quaternion projR
) {
    public static StaticModelGeoDecalProjection read(DataSource source) throws IOException {
        var projS = Quaternion.read(source);
        var projT = Quaternion.read(source);
        var projR = Quaternion.read(source);
        return new StaticModelGeoDecalProjection(projS, projT, projR);
    }
}
