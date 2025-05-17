package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record VegetationSurface(
    String materialName,
    float unknown,
    List<VegetationLod> lods
) {
    public static VegetationSurface read(DataSource source, int numLods) throws IOException {
        var materialName = source.readPString();
        var unknown = source.readFloat();
        var lods = source.readObjects(numLods, VegetationLod::read);

        return new VegetationSurface(
            materialName,
            unknown,
            lods
        );
    }
}
