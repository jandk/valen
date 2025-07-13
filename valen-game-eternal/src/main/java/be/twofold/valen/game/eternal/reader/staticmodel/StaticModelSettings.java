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
    public static StaticModelSettings read(BinaryReader reader) throws IOException {
        var lightmapSurfaceAreaSqrt = reader.readFloat();
        var lighmapWidth = reader.readInt();
        var lighmapHeight = reader.readInt();
        var textureAxes = reader.readObjects(reader.readInt(), StaticModelTextureAxis::read);

        return new StaticModelSettings(
            lightmapSurfaceAreaSqrt,
            lighmapWidth,
            lighmapHeight,
            textureAxes
        );
    }
}
