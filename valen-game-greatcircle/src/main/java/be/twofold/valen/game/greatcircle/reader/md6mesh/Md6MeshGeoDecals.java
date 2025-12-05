package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.*;

record Md6MeshGeoDecals(
    String materialName,
    Ints geoDecalCounts,
    List<Ints> geoDecalIndices
) {
    static Md6MeshGeoDecals read(BinaryReader reader) throws IOException {
        var materialName = reader.readPString();
        var numStreams = reader.readInt();
        var geoDecalCounts = reader.readInts(numStreams);

        var geoDecalIndices = new ArrayList<Ints>();
        for (var stream = 0; stream < numStreams; stream++) {
            geoDecalIndices.add(reader.readInts(geoDecalCounts.get(stream)));
        }

        return new Md6MeshGeoDecals(
            materialName,
            geoDecalCounts,
            geoDecalIndices
        );
    }
}
