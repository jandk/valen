package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

record Md6MeshGeoDecals(
    String materialName,
    int[] geoDecalCounts,
    int[][] geoDecalIndices
) {
    static Md6MeshGeoDecals read(BinaryReader reader) throws IOException {
        var materialName = reader.readPString();
        var numStreams = reader.readInt();
        var geoDecalCounts = reader.readInts(numStreams);

        var geoDecalIndices = new int[numStreams][];
        for (var stream = 0; stream < numStreams; stream++) {
            geoDecalIndices[stream] = reader.readInts(geoDecalCounts[stream]);
        }

        return new Md6MeshGeoDecals(
            materialName,
            geoDecalCounts,
            geoDecalIndices
        );
    }
}
