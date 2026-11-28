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
    public static Mega2Header read(BinaryReader reader) throws IOException {
        reader.expectInt(0xA63FBB21); // magic
        reader.expectInt(2); // version
        reader.expectInt(16); // unknown08
        int levelCount = reader.readInt();
        int virtualXResolution = reader.readInt();
        int virtualYResolution = reader.readInt();
        int virtualXBlockCount = reader.readInt();
        int virtualYBlockCount = reader.readInt();
        int xResolution = reader.readInt();
        int yResolution = reader.readInt();
        int xBlockCount = reader.readInt();
        int yBlockCount = reader.readInt();
        int gridLog2 = reader.readInt();
        int quadtreeLevelCount = reader.readInt();
        long pointerOffset = reader.readLong();
        long quadtreeOffset = reader.readLong();
        int pointerCount = reader.readInt();
        int quadtreeCount = reader.readInt();

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
