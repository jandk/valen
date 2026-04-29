package be.twofold.valen.game.eternal.reader.md6model;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

public record Md6ModelBoneInfo(
    Bytes jointRemap,
    Bounds defaultBounds,
    Floats maxLodDeviations
) {
    public static Md6ModelBoneInfo read(BinarySource source) throws IOException {
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
