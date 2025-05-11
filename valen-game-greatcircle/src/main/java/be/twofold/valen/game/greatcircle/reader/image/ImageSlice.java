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
    public static ImageSlice read(DataSource source) throws IOException{
        var level = source.readInt();
        var slice = source.readInt();
        var width = source.readInt();
        var height = source.readInt();
        var unknown1 = source.readInt();
        var decompressedSize = source.readInt();
        var hash = source.readLong();
        var streamed = source.readBoolInt();
        var size = source.readInt();
        var cumulativeSize = source.readInt();

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
