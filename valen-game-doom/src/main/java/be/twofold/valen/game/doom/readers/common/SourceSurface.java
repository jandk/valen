package be.twofold.valen.game.doom.readers.common;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;

public record SourceSurface(
    String material,
    int renderSurface,
    int firstVertex,
    int lastVertex
) {
    public static SourceSurface read(BinaryReader reader) throws IOException {
        reader.order(ByteOrder.LITTLE_ENDIAN);
        var material = reader.readString(StringFormat.INT_LENGTH);

        reader.order(ByteOrder.BIG_ENDIAN);
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
