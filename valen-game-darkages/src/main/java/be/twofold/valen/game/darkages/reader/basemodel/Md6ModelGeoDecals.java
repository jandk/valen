package be.twofold.valen.game.darkages.reader.basemodel;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Md6ModelGeoDecals(
    String geoDecalMaterialName,
    Ints streamSizes,
    Ints decals
) {
    public static Md6ModelGeoDecals read(BinaryReader reader) throws IOException {
        var geoDecalMaterialName = reader.readString(StringFormat.INT_LENGTH);
        var streamSizes = reader.readInts(reader.readInt());
        var decals = reader.readInts(streamSizes.stream().sum());

        return new Md6ModelGeoDecals(
            geoDecalMaterialName,
            streamSizes,
            decals
        );
    }
}
