package be.twofold.valen.game.doom.readers.model;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StaticModel(
    ModelHeader header,
    List<ModelSurface> surfaces
) {
    public static final int MAGIC = 0x1B4C4D42;

    public static StaticModel read(BinarySource source) throws IOException {
        var header = ModelHeader.read(source);
        var surfaces = source.readObjects(header.numSurfaces(), ModelSurface::read);

        return new StaticModel(header, surfaces);
    }
}
