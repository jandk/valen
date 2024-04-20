package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.util.*;

import java.util.*;

public record StaticModelGeoDecals(
    List<StaticModelGeoDecalProjection> projections,
    String materialName
) {
    public static StaticModelGeoDecals read(BetterBuffer buffer) {
        var numGeoDecals = buffer.getInt();
        var projections = buffer.getStructs(numGeoDecals, StaticModelGeoDecalProjection::read);
        var materialName = buffer.getString();
        return new StaticModelGeoDecals(projections, materialName);
    }
}
