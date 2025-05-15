package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Md6ModelSurfaceInfo(
    String materialName,
    int renderSurface,
    int firstVertex,
    int lastVertex
) {
    public static Md6ModelSurfaceInfo read(DataSource source) throws IOException {
        var materialName = source.readPString();
        var renderSurface = source.readInt();
        var firstVertex = source.readInt();
        var lastVertex = source.readInt();

        return new Md6ModelSurfaceInfo(
            materialName,
            renderSurface,
            firstVertex,
            lastVertex
        );
    }
}
