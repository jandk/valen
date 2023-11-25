package be.twofold.valen.reader.model;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;

public record ModelGeoDecalProjection(
    Vector4 projS,
    Vector4 projT
) {
    public static ModelGeoDecalProjection read(BetterBuffer buffer) {
        Vector4 projS = buffer.getVector4();
        Vector4 projT = buffer.getVector4();
        return new ModelGeoDecalProjection(projS, projT);
    }
}
