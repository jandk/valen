package be.twofold.valen.reader.md6skl;

import be.twofold.valen.*;

public record Md6SkeletonHeader(
    short unkOffset,
    short boneNamesOffset,
    short boneCount,
    short[] offsets
) {
    public static Md6SkeletonHeader read(BetterBuffer buffer) {
        short unkOffset = buffer.getShort();
        buffer.expectShort((short) 0);
        short boneNamesOffset = buffer.getShort();
        short boneCount = buffer.getShort();
        short[] offsets = buffer.getShorts(24);
        return new Md6SkeletonHeader(unkOffset, boneNamesOffset, boneCount, offsets);
    }

    public int emptyBones() {
        return 8 - boneCount % 8;
    }
}
