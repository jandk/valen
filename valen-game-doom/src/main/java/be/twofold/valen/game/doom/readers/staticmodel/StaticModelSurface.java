package be.twofold.valen.game.doom.readers.staticmodel;

import be.twofold.valen.game.doom.readers.common.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record StaticModelSurface(
    String material,
    int materialChecksum,
    int materialNum,
    int binaryModelId,
    int numPatchMaterials,
    List<String> patchMaterials,
    Triangles geometry
) {
    public static StaticModelSurface read(BinaryReader reader, int version) throws IOException {
        reader.order(ByteOrder.LITTLE_ENDIAN);
        var material = reader.readString(StringFormat.INT_LENGTH);

        reader.order(ByteOrder.BIG_ENDIAN);
        int materialChecksum = version > 0x1A ? reader.readInt() : 0;
        int materialNum = reader.readInt();
        int binaryModelId = reader.readInt();
        int numPatchMaterials = reader.readInt();

        reader.order(ByteOrder.LITTLE_ENDIAN);
        var patchMaterials = reader.readStrings(numPatchMaterials, StringFormat.INT_LENGTH);
        var geometry = Triangles.read(reader);

        reader.expectInt(StaticModel.MAGIC | version << 24);

        return new StaticModelSurface(
            material,
            materialChecksum,
            materialNum,
            binaryModelId,
            numPatchMaterials,
            patchMaterials,
            geometry
        );
    }
}
