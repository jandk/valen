package be.twofold.valen.game.doom.readers.common;

import be.twofold.valen.game.doom.readers.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record SourceSurface(
    String material,
    int renderSurface,
    int firstVertex,
    int lastVertex
) {
    public static SourceSurface read(BinaryReader reader) throws IOException {
        var material = IdStrings.read(reader);
        var renderSurface = reader.readInt();
        var firstVertex = reader.readInt();
        var lastVertex = reader.readInt();

        return new SourceSurface(
            material,
            renderSurface,
            firstVertex,
            lastVertex
        );
    }
}
