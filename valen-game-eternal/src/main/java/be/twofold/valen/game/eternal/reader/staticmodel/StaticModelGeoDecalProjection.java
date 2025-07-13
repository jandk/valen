package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

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
