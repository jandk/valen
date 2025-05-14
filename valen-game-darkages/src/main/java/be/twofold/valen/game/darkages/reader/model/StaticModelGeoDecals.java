package be.twofold.valen.game.darkages.reader.model;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StaticModelGeoDecals(
    List<StaticModelGeoDecalProjection> projections,
    String materialName,
    int tintStartOffset
) {
    public static StaticModelGeoDecals read(DataSource source) throws IOException {
        var projections = source.readObjects(source.readInt(), StaticModelGeoDecalProjection::read);
        var materialName = source.readPString();
        var tintStartOffset = source.readInt();

        return new StaticModelGeoDecals(
            projections,
            materialName,
            tintStartOffset
        );
    }
}
