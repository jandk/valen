package be.twofold.valen.game.darkages.reader.basemodel;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Md6ModelMeshWound(
    String name,
    int meshIndex,
    Ints offsets
) {
    public static Md6ModelMeshWound read(BinaryReader reader) throws IOException {
        var name = reader.readString(StringFormat.INT_LENGTH);
        var meshIndex = reader.readInt();
        var offsets = reader.readInts(5);

        return new Md6ModelMeshWound(
            name,
            meshIndex,
            offsets
        );
    }
}
