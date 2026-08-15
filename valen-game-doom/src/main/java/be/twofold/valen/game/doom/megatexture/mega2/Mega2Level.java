package be.twofold.valen.game.doom.megatexture.mega2;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Mega2Level(
    int xBlockIndex,
    int yBlockIndex,
    int xBlockCount,
    int yBlockCount,
    int quadtreeIndex,
    int quadtreeCount
) {
    public static Mega2Level read(BinarySource source) throws IOException {
        int xBlockIndex = source.readInt();
        int yBlockIndex = source.readInt();
        int xBlockCount = source.readInt();
        int yBlockCount = source.readInt();
        int quadtreeIndex = source.readInt();
        int quadtreeCount = source.readInt();

        return new Mega2Level(
            xBlockIndex,
            yBlockIndex,
            xBlockCount,
            yBlockCount,
            quadtreeIndex,
            quadtreeCount
        );
    }
}
