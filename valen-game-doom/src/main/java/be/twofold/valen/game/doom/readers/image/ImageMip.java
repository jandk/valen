package be.twofold.valen.game.doom.readers.image;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;

public record ImageMip(
    int mip,
    int face,
    int width,
    int height,
    int size,
    Bytes data
) {
    public static ImageMip read(BinaryReader reader) throws IOException {
        reader.order(ByteOrder.BIG_ENDIAN);

        var mip = reader.readInt();
        var face = reader.readInt();
        var width = reader.readInt();
        var height = reader.readInt();
        var size = reader.readInt();
        var data = reader.readBytes(size);

        return new ImageMip(
            mip,
            face,
            width,
            height,
            size,
            data
        );
    }
}
