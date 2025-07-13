package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.core.io.*;

import java.io.*;

public record VegetationHeader(
    int numSurfaces,
    int numLods
) {
    public static VegetationHeader read(BinaryReader reader) throws IOException {
        var numSurfaces = reader.readInt();
        var numLods = reader.readInt();

        return new VegetationHeader(
            numSurfaces,
            numLods
        );
    }
}
