package be.twofold.valen.reader.model;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record ModelGeoDecalProjection(
    Quaternion projS,
    Quaternion projT
) {
    public static ModelGeoDecalProjection read(BetterBuffer buffer) {
        Quaternion projS = buffer.getQuaternion();
        Quaternion projT = buffer.getQuaternion();
        return new ModelGeoDecalProjection(projS, projT);
    }
}
