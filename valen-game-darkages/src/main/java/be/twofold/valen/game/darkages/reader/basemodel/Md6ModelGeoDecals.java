package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;
import java.util.*;

public record Md6ModelGeoDecals(
    String geoDecalMaterialName,
    int[] streamSizes,
    int[] decals
) {
    public static Md6ModelGeoDecals read(BinaryReader reader) throws IOException {
        var geoDecalMaterialName = reader.readPString();
        var streamSizes = reader.readInts(reader.readInt());
        var decals = reader.readInts(Arrays.stream(streamSizes).sum());

        return new Md6ModelGeoDecals(
            geoDecalMaterialName,
            streamSizes,
            decals
        );
    }
}
