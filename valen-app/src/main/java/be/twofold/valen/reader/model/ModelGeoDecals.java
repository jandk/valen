package be.twofold.valen.reader.model;

import be.twofold.valen.core.util.*;

import java.util.*;

public record ModelGeoDecals(
    List<ModelGeoDecalProjection> projections,
    String materialName
) {
    public static ModelGeoDecals read(BetterBuffer buffer) {
        int numGeoDecals = buffer.getInt();
        List<ModelGeoDecalProjection> projections = buffer.getStructs(numGeoDecals, ModelGeoDecalProjection::read);
        String materialName = buffer.getString();
        return new ModelGeoDecals(projections, materialName);
    }
}
