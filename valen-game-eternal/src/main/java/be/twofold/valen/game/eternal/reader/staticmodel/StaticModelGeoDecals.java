package be.twofold.valen.game.eternal.reader.staticmodel;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StaticModelGeoDecals(
    List<StaticModelGeoDecalProjection> projections,
    String materialName,
    int tintStartOffset
) {
    public static StaticModelGeoDecals read(BinarySource source) throws IOException {
        var projections = source.readObjects(source.readInt(), StaticModelGeoDecalProjection::read);
        var materialName = source.readString(StringFormat.INT_LENGTH);
        var tintStartOffset = source.readInt();

        return new StaticModelGeoDecals(
            projections,
            materialName,
            tintStartOffset
        );
    }
}
