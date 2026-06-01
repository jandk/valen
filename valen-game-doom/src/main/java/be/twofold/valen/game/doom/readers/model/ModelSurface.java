package be.twofold.valen.game.doom.readers.model;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record ModelSurface(
    String material,
    int unk1,
    int unk2,
    int unk3,
    int unk4,
    List<String> unk5,
    Triangles triangles
) {
    public static ModelSurface read(BinarySource source) throws IOException {
        source.order(ByteOrder.LITTLE_ENDIAN);
        var material = source.readString(StringFormat.INT_LENGTH);

        source.order(ByteOrder.BIG_ENDIAN);
        int unk1 = source.readInt();
        int unk2 = source.readInt();
        int unk3 = source.readInt();
        int unk4 = source.readInt();

        source.order(ByteOrder.LITTLE_ENDIAN);
        var unk5 = source.readStrings(unk4, StringFormat.INT_LENGTH);
        var triangles = Triangles.read(source);

        source.expectInt(StaticModel.MAGIC);

        return new ModelSurface(
            material,
            unk1,
            unk2,
            unk3,
            unk4,
            unk5,
            triangles
        );
    }
}
