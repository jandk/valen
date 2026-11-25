package be.twofold.valen.game.doom.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourcesIndexHeader(
    int size,
    int count
) {
    public static ResourcesIndexHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x0553_4552);
        var size = reader.readInt();
        reader.expectZeros(24);
        var count = reader.readInt();

        return new ResourcesIndexHeader(
            size,
            count
        );
    }
}
