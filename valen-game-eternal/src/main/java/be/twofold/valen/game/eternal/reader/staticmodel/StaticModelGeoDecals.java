package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StaticModelGeoDecals(
    List<StaticModelGeoDecalProjection> projections,
    String materialName,
    int tintStartOffset
) {
    public static StaticModelGeoDecals read(BinaryReader reader) throws IOException {
        var projections = reader.readObjects(reader.readInt(), StaticModelGeoDecalProjection::read);
        var materialName = reader.readPString();
        var tintStartOffset = reader.readInt();

        return new StaticModelGeoDecals(
            projections,
            materialName,
            tintStartOffset
        );
    }
}
