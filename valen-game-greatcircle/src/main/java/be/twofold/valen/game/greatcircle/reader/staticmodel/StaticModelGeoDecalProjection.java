package be.twofold.valen.game.greatcircle.reader.staticmodel;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

public record StaticModelGeoDecalProjection(
    Quaternion projS,
    Quaternion projT
) {
    public static StaticModelGeoDecalProjection read(BinaryReader reader) throws IOException {
        var projS = Quaternion.read(reader);
        var projT = Quaternion.read(reader);
        return new StaticModelGeoDecalProjection(projS, projT);
    }
}
