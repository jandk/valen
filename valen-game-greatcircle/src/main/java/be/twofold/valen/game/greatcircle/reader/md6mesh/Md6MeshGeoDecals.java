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
    static Md6MeshGeoDecals read(BinarySource source) throws IOException {
        var materialName = source.readString(StringFormat.INT_LENGTH);
        var numStreams = source.readInt();
        var geoDecalCounts = source.readInts(numStreams);

        var geoDecalIndices = new ArrayList<Ints>();
        for (var stream = 0; stream < numStreams; stream++) {
            geoDecalIndices.add(source.readInts(geoDecalCounts.get(stream)));
        }

        return new Md6MeshGeoDecals(
            materialName,
            geoDecalCounts,
            geoDecalIndices
        );
    }
}
