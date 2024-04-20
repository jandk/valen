package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record StaticModelGeoDecalProjection(
    Quaternion projS,
    Quaternion projT
) {
    public static StaticModelGeoDecalProjection read(BetterBuffer buffer) {
        var projS = Quaternion.read(buffer);
        var projT = Quaternion.read(buffer);
        return new StaticModelGeoDecalProjection(projS, projT);
    }
}
