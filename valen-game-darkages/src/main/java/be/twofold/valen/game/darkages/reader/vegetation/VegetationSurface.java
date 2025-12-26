package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record VegetationSurface(
    String materialName,
    int unknown,
    List<VegetationLod> lods
) {
    public static VegetationSurface read(BinarySource source, int numLods) throws IOException {
        var materialName = source.readString(StringFormat.INT_LENGTH);
        var unknown = source.readInt();
        var lods = source.readObjects(numLods, VegetationLod::read);

        return new VegetationSurface(
            materialName,
            unknown,
            lods
        );
    }
}
