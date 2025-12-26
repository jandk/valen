package be.twofold.valen.game.eternal.reader.md6model;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Md6ModelGeoDecals(
    String materialName,
    Ints geoDecalCounts,
    List<Ints> geoDecalIndices
) {
    public static Md6ModelGeoDecals read(BinarySource source) throws IOException {
        var materialName = source.readString(StringFormat.INT_LENGTH);
        var numStreams = source.readInt();
        var geoDecalCounts = source.readInts(numStreams);

        var geoDecalIndices = new ArrayList<Ints>();
        for (var stream = 0; stream < numStreams; stream++) {
            geoDecalIndices.add(source.readInts(geoDecalCounts.get(stream)));
        }

        return new Md6ModelGeoDecals(
            materialName,
            geoDecalCounts,
            geoDecalIndices
        );
    }
}
