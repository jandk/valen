package be.twofold.valen.game.greatcircle.reader.staticmodel;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

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
