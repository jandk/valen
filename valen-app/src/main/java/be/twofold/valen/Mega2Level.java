package be.twofold.valen;

import be.twofold.valen.core.util.*;

public record Mega2Level(
    int blockXIndex,
    int blockYIndex,
    int blockXCount,
    int blockYCount,
    int treeIndex,
    int treeCount
) {
    public static final int BYTES = 24;

    public static Mega2Level read(BetterBuffer buffer) {
        int blockXIndex = buffer.getInt();
        int blockYIndex = buffer.getInt();
        int blockXCount = buffer.getInt();
        int blockYCount = buffer.getInt();
        int treeIndex = buffer.getInt();
        int treeCount = buffer.getInt();

        return new Mega2Level(
            blockXIndex,
            blockYIndex,
            blockXCount,
            blockYCount,
            treeIndex,
            treeCount
        );
    }
}
