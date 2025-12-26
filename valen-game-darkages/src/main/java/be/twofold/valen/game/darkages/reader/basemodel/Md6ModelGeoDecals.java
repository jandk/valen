package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;

public record Md6ModelGeoDecals(
    String geoDecalMaterialName,
    Ints streamSizes,
    Ints decals
) {
    public static Md6ModelGeoDecals read(BinarySource source) throws IOException {
        var geoDecalMaterialName = source.readString(StringFormat.INT_LENGTH);
        var streamSizes = source.readInts(source.readInt());
        var decals = source.readInts(streamSizes.stream().sum());

        return new Md6ModelGeoDecals(
            geoDecalMaterialName,
            streamSizes,
            decals
        );
    }
}
