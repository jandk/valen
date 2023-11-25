package be.twofold.valen.reader.md6;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record Md6BoneInfo(
    int numBones,
    byte[] bones,
    Vector3 min,
    Vector3 max,
    float unknown1,
    float unknown2
) {
    public static Md6BoneInfo read(BetterBuffer buffer) {
        int numBones = buffer.getShort();
        byte[] bones = buffer.getBytes(numBones);

        Vector3 min = buffer.getVector3();
        Vector3 max = buffer.getVector3();
        buffer.expectInt(5);

        buffer.expectInt(0);
        float unknown1 = buffer.getFloat();
        float unknown2 = buffer.getFloat();

        for (int i = 0; i < 12; i++) {
            buffer.expectInt(0);
        }

        return new Md6BoneInfo(numBones, bones, min, max, unknown1, unknown2);
    }
}
