package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StaticModelGeoDecals(
    List<StaticModelGeoDecalProjection> projections,
    String materialName
) {
    public static StaticModelGeoDecals read(DataSource source) throws IOException {
        var numGeoDecals = source.readInt();
        var projections = source.readStructs(numGeoDecals, StaticModelGeoDecalProjection::read);
        var materialName = source.readPString();
        return new StaticModelGeoDecals(projections, materialName);
    }
}
