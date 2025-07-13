package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

public record Md6ModelMaterialInfo(
    String mtrName,
    int renderSurface,
    int firstVertex,
    int lastVertex
) {
    public static Md6ModelMaterialInfo read(BinaryReader reader) throws IOException {
        var mtrName = reader.readPString();
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
