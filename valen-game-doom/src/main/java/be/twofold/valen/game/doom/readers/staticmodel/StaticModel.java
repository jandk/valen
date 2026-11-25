package be.twofold.valen.game.doom.readers.staticmodel;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StaticModel(
        StaticModelHeader header,
        List<StaticModelSurface> surfaces
) {
    public static final int MAGIC = 0x1B4C4D42;

    public static StaticModel read(BinaryReader reader) throws IOException {
        var header = StaticModelHeader.read(reader);
        var surfaces = reader.readObjects(header.numSurfaces(), StaticModelSurface::read);

        return new StaticModel(header, surfaces);
    }
}
