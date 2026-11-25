package be.twofold.valen.game.darkages.reader.vegetation;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record VegetationSurface(
    String materialName,
    int unknown,
    List<VegetationLod> lods
) {
    public static VegetationSurface read(BinaryReader reader, int numLods) throws IOException {
        var materialName = reader.readString(StringFormat.INT_LENGTH);
        var unknown = reader.readInt();
        var lods = reader.readObjects(numLods, VegetationLod::read);

        return new VegetationSurface(
            materialName,
            unknown,
            lods
        );
    }
}
