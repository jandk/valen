package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record StaticModelGeoDecalProjection(
    Quaternion projS,
    Quaternion projT
) {
    public static StaticModelGeoDecalProjection read(DataSource source) throws IOException {
        var projS = source.readQuaternion();
        var projT = source.readQuaternion();
        return new StaticModelGeoDecalProjection(projS, projT);
    }
}
