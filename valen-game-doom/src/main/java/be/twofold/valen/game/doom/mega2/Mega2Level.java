package be.twofold.valen.game.doom.mega2;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Mega2Level(
    int blockXIndex,
    int blockYIndex,
    int blockXCount,
    int blockYCount,
    int treeIndex,
    int treeCount
) {
    public static Mega2Level read(BinarySource source) throws IOException {
        int blockXIndex = source.readInt();
        int blockYIndex = source.readInt();
        int blockXCount = source.readInt();
        int blockYCount = source.readInt();
        int treeIndex = source.readInt();
        int treeCount = source.readInt();

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
