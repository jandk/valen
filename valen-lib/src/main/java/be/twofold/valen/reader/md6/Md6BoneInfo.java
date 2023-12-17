package be.twofold.valen.reader.md6;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record Md6BoneInfo(
    int numBones,
    byte[] bones,
    Bounds bounds,
    float unknown1,
    float unknown2
) {
    public static Md6BoneInfo read(BetterBuffer buffer) {
        var numBones = (int) buffer.getShort();
        var bones = buffer.getBytes(numBones);
        var bounds = Bounds.read(buffer);
        buffer.expectInt(5);
        buffer.expectInt(0);
        var unknown1 = buffer.getFloat();
        var unknown2 = buffer.getFloat();
        for (var i = 0; i < 12; i++) {
            buffer.expectInt(0);
        }

        return new Md6BoneInfo(numBones, bones, bounds, unknown1, unknown2);
    }
}
