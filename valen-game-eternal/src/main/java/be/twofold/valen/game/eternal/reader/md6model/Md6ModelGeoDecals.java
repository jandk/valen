package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

public record Md6ModelGeoDecals(
    String materialName,
    int[] geoDecalCounts,
    int[][] geoDecalIndices
) {
    public static Md6ModelGeoDecals read(BinaryReader reader) throws IOException {
        var materialName = reader.readPString();
        var numStreams = reader.readInt();
        var geoDecalCounts = reader.readInts(numStreams);

        var geoDecalIndices = new int[numStreams][];
        for (var stream = 0; stream < numStreams; stream++) {
            geoDecalIndices[stream] = reader.readInts(geoDecalCounts[stream]);
        }

        return new Md6ModelGeoDecals(
            materialName,
            geoDecalCounts,
            geoDecalIndices
        );
    }
}
