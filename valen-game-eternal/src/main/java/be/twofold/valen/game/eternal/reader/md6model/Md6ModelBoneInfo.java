package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Md6ModelBoneInfo(
    byte[] jointRemap,
    Bounds defaultBounds,
    float[] maxLodDeviations
) {
    public static Md6ModelBoneInfo read(DataSource source) throws IOException {
        var jointRemap = source.readBytes(source.readShort());
        var defaultBounds = Bounds.read(source);
        source.expectInt(5); // numLods
        var maxLodDeviations = source.readFloats(5);
        source.expectInt(0); // numBlendShapeNames
        for (int i = 0; i < 9; i++) {
            source.expectInt(0); // padding
        }

        return new Md6ModelBoneInfo(
            jointRemap,
            defaultBounds,
            maxLodDeviations
        );
    }
}
