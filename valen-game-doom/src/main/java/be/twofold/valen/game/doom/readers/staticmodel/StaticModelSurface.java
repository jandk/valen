package be.twofold.valen.game.doom.readers.staticmodel;

import be.twofold.valen.game.doom.readers.common.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record StaticModelSurface(
    String material,
    int unk1,
    int unk2,
    int unk3,
    int unk4,
    List<String> unk5,
    Triangles triangles
) {
    public static StaticModelSurface read(BinaryReader reader) throws IOException {
        reader.order(ByteOrder.LITTLE_ENDIAN);
        var material = reader.readString(StringFormat.INT_LENGTH);

        reader.order(ByteOrder.BIG_ENDIAN);
        int unk1 = reader.readInt();
        int unk2 = reader.readInt();
        int unk3 = reader.readInt();
        int unk4 = reader.readInt();

        reader.order(ByteOrder.LITTLE_ENDIAN);
        var unk5 = reader.readStrings(unk4, StringFormat.INT_LENGTH);
        var triangles = Triangles.read(reader);

        reader.expectInt(StaticModel.MAGIC);

        return new StaticModelSurface(
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
