package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.core.io.*;

import java.io.*;

public record VegetationHeader(
    int numSurfaces,
    int numLods
) {
    public static VegetationHeader read(BinarySource source) throws IOException {
        var numSurfaces = source.readInt();
        var numLods = source.readInt();

        return new VegetationHeader(
            numSurfaces,
            numLods
        );
    }
}
