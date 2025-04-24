package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;

import java.io.*;

record Md6MeshGeoDecals(
    String materialName,
    int[] geoDecalCounts,
    int[][] geoDecalIndices
) {
    static Md6MeshGeoDecals read(DataSource source) throws IOException {
        var materialName = source.readPString();
        var numStreams = source.readInt();
        var geoDecalCounts = source.readInts(numStreams);

        var geoDecalIndices = new int[numStreams][];
        for (var stream = 0; stream < numStreams; stream++) {
            geoDecalIndices[stream] = source.readInts(geoDecalCounts[stream]);
        }

        return new Md6MeshGeoDecals(
            materialName,
            geoDecalCounts,
            geoDecalIndices
        );
    }
}
