package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.*;

public record Md6ModelGeoDecals(
    String materialName,
    Ints geoDecalCounts,
    List<Ints> geoDecalIndices
) {
    public static Md6ModelGeoDecals read(BinaryReader reader) throws IOException {
        var materialName = reader.readPString();
        var numStreams = reader.readInt();
        var geoDecalCounts = reader.readIntsStruct(numStreams);

        var geoDecalIndices = new ArrayList<Ints>();
        for (var stream = 0; stream < numStreams; stream++) {
            geoDecalIndices.add(reader.readIntsStruct(geoDecalCounts.getInt(stream)));
        }

        return new Md6ModelGeoDecals(
            materialName,
            geoDecalCounts,
            geoDecalIndices
        );
    }
}
