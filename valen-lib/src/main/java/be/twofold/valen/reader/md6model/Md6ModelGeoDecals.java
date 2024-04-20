package be.twofold.valen.reader.md6model;

import be.twofold.valen.core.util.*;

public record Md6ModelGeoDecals(
    String materialName,
    int[] geoDecalCounts,
    int[][] geoDecalIndices
) {
    public static Md6ModelGeoDecals read(BetterBuffer buffer) {
        var materialName = buffer.getString();
        var numStreams = buffer.getInt();
        if (materialName.isEmpty() && numStreams == 0) {
            return new Md6ModelGeoDecals(materialName, new int[0], new int[0][]);
        }

        var counts = buffer.getInts(numStreams);
        var indices = new int[numStreams][];
        for (var stream = 0; stream < numStreams; stream++) {
            indices[stream] = buffer.getInts(counts[stream]);
        }

        return new Md6ModelGeoDecals(materialName, counts, indices);
    }
}
