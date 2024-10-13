package be.twofold.valen.game.doom.mega2;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Mega2Header(
    int signature,
    int version,
    int unknown2,
    int levelCount,
    int totalXRes,
    int totalYRes,
    int totalXBlocks,
    int totalYBlocks,
    int xRes,
    int yRes,
    int xBlocks,
    int yBlocks,
    int unknown7,
    int quadtreeLevelCount,
    long pointerOffset,
    long quadtreeOffset,
    int pointerCount,
    int quadtreeCount
) {
    public static Mega2Header read(DataSource source) throws IOException {
        int signature = source.readInt();
        int version = source.readInt();
        int unknown2 = source.readInt();
        int levelCount = source.readInt();
        int totalXRes = source.readInt();
        int totalYRes = source.readInt();
        int totalXBlocks = source.readInt();
        int totalYBlocks = source.readInt();
        int xRes = source.readInt();
        int yRes = source.readInt();
        int xBlocks = source.readInt();
        int yBlocks = source.readInt();
        int unknown7 = source.readInt();
        int quadtreeLevelCount = source.readInt();
        long pointerOffset = source.readLong();
        long quadtreeOffset = source.readLong();
        int pointerCount = source.readInt();
        int quadtreeCount = source.readInt();

        return new Mega2Header(
            signature,
            version,
            unknown2,
            levelCount,
            totalXRes,
            totalYRes,
            totalXBlocks,
            totalYBlocks,
            xRes,
            yRes,
            xBlocks,
            yBlocks,
            unknown7,
            quadtreeLevelCount,
            pointerOffset,
            quadtreeOffset,
            pointerCount,
            quadtreeCount
        );
    }
}
