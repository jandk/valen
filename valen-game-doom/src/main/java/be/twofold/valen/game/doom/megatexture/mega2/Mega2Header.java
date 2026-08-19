package be.twofold.valen.game.doom.megatexture.mega2;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Mega2Header(
    int levelCount,
    int virtualXResolution,
    int virtualYResolution,
    int virtualXBlockCount,
    int virtualYBlockCount,
    int xResolution,
    int yResolution,
    int xBlockCount,
    int yBlockCount,
    int gridLog2,
    int quadtreeLevelCount,
    long pointerOffset,
    long quadtreeOffset,
    int pointerCount,
    int quadtreeCount
) {
    public static Mega2Header read(BinarySource source) throws IOException {
        source.expectInt(0xA63FBB21); // magic
        source.expectInt(2); // version
        source.expectInt(16); // unknown08
        int levelCount = source.readInt();
        int virtualXResolution = source.readInt();
        int virtualYResolution = source.readInt();
        int virtualXBlockCount = source.readInt();
        int virtualYBlockCount = source.readInt();
        int xResolution = source.readInt();
        int yResolution = source.readInt();
        int xBlockCount = source.readInt();
        int yBlockCount = source.readInt();
        int gridLog2 = source.readInt();
        int quadtreeLevelCount = source.readInt();
        long pointerOffset = source.readLong();
        long quadtreeOffset = source.readLong();
        int pointerCount = source.readInt();
        int quadtreeCount = source.readInt();

        return new Mega2Header(
            levelCount,
            virtualXResolution,
            virtualYResolution,
            virtualXBlockCount,
            virtualYBlockCount,
            xResolution,
            yResolution,
            xBlockCount,
            yBlockCount,
            gridLog2,
            quadtreeLevelCount,
            pointerOffset,
            quadtreeOffset,
            pointerCount,
            quadtreeCount
        );
    }

    public boolean equalGeometry(Mega2Header other) {
        return levelCount == other.levelCount
            && virtualXResolution == other.virtualXResolution
            && virtualYResolution == other.virtualYResolution
            && virtualXBlockCount == other.virtualXBlockCount
            && virtualYBlockCount == other.virtualYBlockCount
            && xResolution == other.xResolution
            && yResolution == other.yResolution
            && xBlockCount == other.xBlockCount
            && yBlockCount == other.yBlockCount
            && gridLog2 == other.gridLog2
            && quadtreeLevelCount == other.quadtreeLevelCount
            && quadtreeCount == other.quadtreeCount;
    }
}
