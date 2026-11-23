package be.twofold.valen.game.eternal.reader.md6model;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Md6ModelMaterialInfo(
    String mtrName,
    int renderSurface,
    int firstVertex,
    int lastVertex
) {
    public static Md6ModelMaterialInfo read(BinaryReader reader) throws IOException {
        var mtrName = reader.readString(StringFormat.INT_LENGTH);
        var renderSurface = reader.readInt();
        var firstVertex = reader.readInt();
        var lastVertex = reader.readInt();

        return new Md6ModelMaterialInfo(
            mtrName,
            renderSurface,
            firstVertex,
            lastVertex
        );
    }
}
