package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StaticModelGeoDecals(
    List<StaticModelGeoDecalProjection> projections,
    String materialName,
    int tintStartOffset
) {
    public static StaticModelGeoDecals read(DataSource source) throws IOException {
        var projections = source.readStructs(source.readInt(), StaticModelGeoDecalProjection::read);
        var materialName = source.readPString();
        var tintStartOffset = source.readInt();

        return new StaticModelGeoDecals(
            projections,
            materialName,
            tintStartOffset
        );
    }
}
