package be.twofold.valen.reader.model;

import be.twofold.valen.core.util.*;

import java.util.*;

public record ModelGeoDecals(
    List<ModelGeoDecalProjection> projections,
    String materialName
) {
    public static ModelGeoDecals read(BetterBuffer buffer) {
        var numGeoDecals = buffer.getInt();
        var projections = buffer.getStructs(numGeoDecals, ModelGeoDecalProjection::read);
        var materialName = buffer.getString();
        return new ModelGeoDecals(projections, materialName);
    }
}
