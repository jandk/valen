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
    public static StaticModelSettings read(DataSource source) throws IOException {
        var lightmapSurfaceAreaSqrt = source.readFloat();
        var lightmapWidth = source.readInt();
        var lightmapHeight = source.readInt();
        var textureAxes = source.readObjects(source.readInt(), StaticModelTextureAxis::read);

        return new StaticModelSettings(
            lightmapSurfaceAreaSqrt,
            lightmapWidth,
            lightmapHeight,
            textureAxes
        );
    }
}
