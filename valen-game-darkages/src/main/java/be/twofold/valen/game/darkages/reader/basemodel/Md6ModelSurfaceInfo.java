package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

public record Md6ModelSurfaceInfo(
    String materialName,
    int renderSurface,
    int firstVertex,
    int lastVertex
) {
    public static Md6ModelSurfaceInfo read(BinaryReader reader) throws IOException {
        var materialName = reader.readPString();
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
