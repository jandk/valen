package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StaticModelSettings(
    float lightmapSurfaceAreaSqrt,
    int lighmapWidth,
    int lighmapHeight,
    List<StaticModelTextureAxis> textureAxes
) {
    public static StaticModelSettings read(DataSource source) throws IOException {
        var lightmapSurfaceAreaSqrt = source.readFloat();
        var lighmapWidth = source.readInt();
        var lighmapHeight = source.readInt();
        var textureAxes = source.readObjects(source.readInt(), StaticModelTextureAxis::read);

        return new StaticModelSettings(
            lightmapSurfaceAreaSqrt,
            lighmapWidth,
            lighmapHeight,
            textureAxes
        );
    }
}
