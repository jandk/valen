package be.twofold.valen.game.darkages.reader.basemodel;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Md6ModelSurfaceInfo(
    String materialName,
    int renderSurface,
    int firstVertex,
    int lastVertex
) {
    public static Md6ModelSurfaceInfo read(BinaryReader reader) throws IOException {
        var materialName = reader.readString(StringFormat.INT_LENGTH);
        var renderSurface = reader.readInt();
        var firstVertex = reader.readInt();
        var lastVertex = reader.readInt();

        return new Md6ModelSurfaceInfo(
            materialName,
            renderSurface,
            firstVertex,
            lastVertex
        );
    }
}
