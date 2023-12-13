package be.twofold.valen.reader.model;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record ModelGeoDecalProjection(
    Quaternion projS,
    Quaternion projT
) {
    public static ModelGeoDecalProjection read(BetterBuffer buffer) {
        Quaternion projS = Quaternion.read(buffer);
        Quaternion projT = Quaternion.read(buffer);
        return new ModelGeoDecalProjection(projS, projT);
    }
}
