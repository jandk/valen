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
    public static Md6ModelBoneInfo read(BinaryReader reader) throws IOException {
        var jointRemap = reader.readBytes(reader.readShort());
        var defaultBounds = Bounds.read(reader);
        reader.expectInt(5); // numLods
        var maxLodDeviations = reader.readFloats(5);
        reader.expectInt(0); // numBlendShapeNames
        reader.expectZeros(36); // padding

        return new Md6ModelBoneInfo(
            jointRemap,
            defaultBounds,
            maxLodDeviations
        );
    }
}
