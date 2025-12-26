package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;

public record Md6ModelMeshWound(
    String name,
    int meshIndex,
    Ints offsets
) {
    public static Md6ModelMeshWound read(BinarySource source) throws IOException {
        var name = source.readString(StringFormat.INT_LENGTH);
        var meshIndex = source.readInt();
        var offsets = source.readInts(5);

        return new Md6ModelMeshWound(
            name,
            meshIndex,
            offsets
        );
    }
}
