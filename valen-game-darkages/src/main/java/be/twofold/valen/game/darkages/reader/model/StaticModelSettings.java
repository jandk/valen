package be.twofold.valen.game.darkages.reader.model;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StaticModelSettings(
    float lightmapSurfaceAreaSqrt,
    int lightmapWidth,
    int lightmapHeight,
    List<StaticModelTextureAxis> textureAxes
) {
    public static StaticModelSettings read(BinaryReader reader) throws IOException {
        var lightmapSurfaceAreaSqrt = reader.readFloat();
        var lightmapWidth = reader.readInt();
        var lightmapHeight = reader.readInt();
        var textureAxes = reader.readObjects(reader.readInt(), StaticModelTextureAxis::read);

        return new StaticModelSettings(
            lightmapSurfaceAreaSqrt,
            lightmapWidth,
            lightmapHeight,
            textureAxes
        );
    }
}
