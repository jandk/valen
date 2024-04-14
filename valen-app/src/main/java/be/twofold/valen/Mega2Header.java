package be.twofold.valen;

import be.twofold.valen.core.util.*;

public record Mega2Header(
    int magic,
    int textureCountMaybe,
    int unknown08,
    int levelCount,
    int xResolution1,
    int yResolution1,
    int xBlockResolution1,
    int yBlockResolution1,
    int xResolution2,
    int yResolution2,
    int xBlockResolution2,
    int yBlockResolution2,
    int unknown44,
    int quadtreeLevelCount,
    long pointerOffset,
    long quadtreeOffset,
    int pointerCount,
    int quadtreeCount
) {
    public static final int BYTES = 80;

    public static Mega2Header read(BetterBuffer buffer) {
        int magic = buffer.getInt();
        int textureCountMaybe = buffer.getInt();
        int unknown08 = buffer.getInt();
        int levelCount = buffer.getInt();
        int xResolution1 = buffer.getInt();
        int yResolution1 = buffer.getInt();
        int xBlockResolution1 = buffer.getInt();
        int yBlockResolution1 = buffer.getInt();
        int xResolution2 = buffer.getInt();
        int yResolution2 = buffer.getInt();
        int xBlockResolution2 = buffer.getInt();
        int yBlockResolution2 = buffer.getInt();
        int unknown44 = buffer.getInt();
        int quadtreeLevelCount = buffer.getInt();
        long pointerOffset = buffer.getLong();
        long quadtreeOffset = buffer.getLong();
        int pointerCount = buffer.getInt();
        int quadtreeCount = buffer.getInt();

        return new Mega2Header(
            magic,
            textureCountMaybe,
            unknown08,
            levelCount,
            xResolution1,
            yResolution1,
            xBlockResolution1,
            yBlockResolution1,
            xResolution2,
            yResolution2,
            xBlockResolution2,
            yBlockResolution2,
            unknown44,
            quadtreeLevelCount,
            pointerOffset,
            quadtreeOffset,
            pointerCount,
            quadtreeCount
        );
    }
}
