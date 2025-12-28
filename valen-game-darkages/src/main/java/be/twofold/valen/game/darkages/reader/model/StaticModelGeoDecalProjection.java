package be.twofold.valen.game.darkages.reader.model;

import wtf.reversed.toolbox.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record StaticModelGeoDecalProjection(
    Quaternion projS,
    Quaternion projT,
    Quaternion projR
) {
    public static StaticModelGeoDecalProjection read(BinarySource source) throws IOException {
        var projS = Quaternion.read(source);
        var projT = Quaternion.read(source);
        var projR = Quaternion.read(source);
        return new StaticModelGeoDecalProjection(projS, projT, projR);
    }
}
