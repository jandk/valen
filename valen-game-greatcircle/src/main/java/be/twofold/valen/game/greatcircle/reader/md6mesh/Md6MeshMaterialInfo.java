package be.twofold.valen.game.greatcircle.reader.md6mesh;

import wtf.reversed.toolbox.io.*;

import java.io.*;

record Md6MeshMaterialInfo(
    String mtrName,
    int renderSurface,
    int firstVertex,
    int lastVertex
) {
    static Md6MeshMaterialInfo read(BinaryReader reader) throws IOException {
        var mtrName = reader.readString(StringFormat.INT_LENGTH);
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
