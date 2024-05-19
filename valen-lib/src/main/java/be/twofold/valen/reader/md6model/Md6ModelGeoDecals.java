package be.twofold.valen.reader.md6model;

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
        if (materialName.isEmpty() && numStreams == 0) {
            return new Md6ModelGeoDecals(materialName, new int[0], new int[0][]);
        }

        var counts = source.readInts(numStreams);
        var indices = new int[numStreams][];
        for (var stream = 0; stream < numStreams; stream++) {
            indices[stream] = source.readInts(counts[stream]);
        }

        return new Md6ModelGeoDecals(materialName, counts, indices);
    }
}
