package be.twofold.valen.reader.md6model;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Md6ModelMaterialInfo(
    String mtrName,
    int renderSurface,
    int firstVertex,
    int lastVertex
) {
    public static Md6ModelMaterialInfo read(DataSource source) throws IOException {
        var mtrName = source.readPString();
        var renderSurface = source.readInt();
        var firstVertex = source.readInt();
        var lastVertex = source.readInt();

        return new Md6ModelMaterialInfo(
            mtrName,
            renderSurface,
            firstVertex,
            lastVertex
        );
    }
}
