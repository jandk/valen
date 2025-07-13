package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

record Md6MeshMaterialInfo(
    String mtrName,
    int renderSurface,
    int firstVertex,
    int lastVertex
) {
    static Md6MeshMaterialInfo read(BinaryReader reader) throws IOException {
        var mtrName = reader.readPString();
        var renderSurface = reader.readInt();
        var firstVertex = reader.readInt();
        var lastVertex = reader.readInt();

        return new Md6MeshMaterialInfo(
            mtrName,
            renderSurface,
            firstVertex,
            lastVertex
        );
    }
}
