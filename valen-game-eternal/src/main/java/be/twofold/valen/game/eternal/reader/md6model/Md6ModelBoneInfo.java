package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;

public record Md6ModelBoneInfo(
    Bytes jointRemap,
    Bounds defaultBounds,
    Floats maxLodDeviations
) {
    public static Md6ModelBoneInfo read(BinaryReader reader) throws IOException {
        var jointRemap = reader.readBytesStruct(reader.readShort());
        var defaultBounds = Bounds.read(reader);
        reader.expectInt(5); // numLods
        var maxLodDeviations = reader.readFloatsStruct(5);
        reader.expectInt(0); // numBlendShapeNames
        for (int i = 0; i < 9; i++) {
            reader.expectInt(0); // padding
        }

        return new Md6ModelBoneInfo(
            jointRemap,
            defaultBounds,
            maxLodDeviations
        );
    }
}
