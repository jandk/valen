package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;

import java.io.*;

record Md6MeshMaterialInfo(
    String mtrName,
    int renderSurface,
    int firstVertex,
    int lastVertex
) {
    static Md6MeshMaterialInfo read(DataSource source) throws IOException {
        var mtrName = source.readPString();
        var renderSurface = source.readInt();
        var firstVertex = source.readInt();
        var lastVertex = source.readInt();

        return new Md6MeshMaterialInfo(
            mtrName,
            renderSurface,
            firstVertex,
            lastVertex
        );
    }
}
