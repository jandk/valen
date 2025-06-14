package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record Md6ModelGeoDecals(
    String geoDecalMaterialName,
    int[] streamSizes,
    int[] decals
) {
    public static Md6ModelGeoDecals read(DataSource source) throws IOException {
        var geoDecalMaterialName = source.readPString();
        var streamSizes = source.readInts(source.readInt());
        var decals = source.readInts(Arrays.stream(streamSizes).sum());

        return new Md6ModelGeoDecals(
            geoDecalMaterialName,
            streamSizes,
            decals
        );
    }
}
