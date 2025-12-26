package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record StaticModelGeoDecalProjection(
    Quaternion projS,
    Quaternion projT
) {
    public static StaticModelGeoDecalProjection read(BinarySource source) throws IOException {
        var projS = Quaternion.read(source);
        var projT = Quaternion.read(source);
        return new StaticModelGeoDecalProjection(projS, projT);
    }
}
