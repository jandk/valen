package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

public record Md6ModelMeshWound(
    String name,
    int meshIndex,
    int[] offsets
) {
    public static Md6ModelMeshWound read(BinaryReader reader) throws IOException {
        var name = reader.readPString();
        var meshIndex = reader.readInt();
        var offsets = reader.readInts(5);

        return new Md6ModelMeshWound(
            name,
            meshIndex,
            offsets
        );
    }
}
