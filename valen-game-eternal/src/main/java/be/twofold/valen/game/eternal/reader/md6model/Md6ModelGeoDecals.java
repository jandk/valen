package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Md6ModelGeoDecals(
    String materialName,
    int[] geoDecalCounts,
    int[][] geoDecalIndices
) {
    public static Md6ModelGeoDecals read(DataSource source) throws IOException {
        var materialName = source.readPString();
        var numStreams = source.readInt();
        var geoDecalCounts = source.readInts(numStreams);

        var geoDecalIndices = new int[numStreams][];
        for (var stream = 0; stream < numStreams; stream++) {
            geoDecalIndices[stream] = source.readInts(geoDecalCounts[stream]);
        }

        return new Md6ModelGeoDecals(
            materialName,
            geoDecalCounts,
            geoDecalIndices
        );
    }
}
