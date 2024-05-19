package be.twofold.valen.reader.md6model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Md6ModelBoneInfo(
    int numBones,
    byte[] bones,
    Bounds bounds,
    float unknown1,
    float unknown2
) {
    public static Md6ModelBoneInfo read(DataSource source) throws IOException {
        var numBones = (int) source.readShort();
        var bones = source.readBytes(numBones);
        var bounds = Bounds.read(source);
        source.expectInt(5);
        source.expectInt(0);
        var unknown1 = source.readFloat();
        var unknown2 = source.readFloat();
        for (var i = 0; i < 12; i++) {
            source.expectInt(0);
        }

        return new Md6ModelBoneInfo(numBones, bones, bounds, unknown1, unknown2);
    }
}
