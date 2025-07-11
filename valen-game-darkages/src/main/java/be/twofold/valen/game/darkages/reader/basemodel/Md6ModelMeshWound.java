package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Md6ModelMeshWound(
    String name,
    int meshIndex,
    int[] offsets
) {
    public static Md6ModelMeshWound read(DataSource source) throws IOException {
        var name = source.readPString();
        var meshIndex = source.readInt();
        var offsets = source.readInts(5);

        return new Md6ModelMeshWound(
            name,
            meshIndex,
            offsets
        );
    }
}
