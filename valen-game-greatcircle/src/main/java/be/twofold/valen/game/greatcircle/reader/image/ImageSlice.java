package be.twofold.valen.game.greatcircle.reader.image;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ImageSlice(
    int level,
    int slice,
    int width,
    int height,
    int unknown1,
    int decompressedSize,
    long hash,
    boolean streamed,
    int size,
    int cumulativeSize
) {
    public static ImageSlice read(BinaryReader reader) throws IOException {
        var level = reader.readInt();
        var slice = reader.readInt();
        var width = reader.readInt();
        var height = reader.readInt();
        var unknown1 = reader.readInt();
        var decompressedSize = reader.readInt();
        var hash = reader.readLong();
        var streamed = reader.readBoolInt();
        var size = reader.readInt();
        var cumulativeSize = reader.readInt();

        return new ImageSlice(
            level,
            slice,
            width,
            height,
            unknown1,
            decompressedSize,
            hash,
            streamed,
            size,
            cumulativeSize
        );
    }
}
