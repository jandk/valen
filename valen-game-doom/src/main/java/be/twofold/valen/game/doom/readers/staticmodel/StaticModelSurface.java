package be.twofold.valen.game.doom.readers.staticmodel;

import be.twofold.valen.game.doom.readers.*;
import be.twofold.valen.game.doom.readers.common.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
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
        var material = IdStrings.read(reader);
        int materialChecksum = version > 0x1A ? reader.readInt() : 0;
        int materialNum = reader.readInt();
        int binaryModelId = reader.readInt();
        int numPatchMaterials = reader.readInt();
        var patchMaterials = IdStrings.read(reader, numPatchMaterials);
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
