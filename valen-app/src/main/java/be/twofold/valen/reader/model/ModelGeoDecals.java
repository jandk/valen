package be.twofold.valen.reader.model;

import java.util.*;

public record ModelGeoDecals(
    String materialName,
    List<ModelGeoDecalProjection> projections
) {
    public ModelGeoDecals {
        Objects.requireNonNull(materialName, "materialName must not be null");
        projections = List.copyOf(projections);
    }
}
