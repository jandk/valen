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
    public static Mega2Level read(BinaryReader reader) throws IOException {
        int xBlockIndex = reader.readInt();
        int yBlockIndex = reader.readInt();
        int xBlockCount = reader.readInt();
        int yBlockCount = reader.readInt();
        int quadtreeIndex = reader.readInt();
        int quadtreeCount = reader.readInt();

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
