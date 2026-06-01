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
    public static ImageMip read(BinarySource source) throws IOException {
        source.order(ByteOrder.BIG_ENDIAN);

        var mip = source.readInt();
        var face = source.readInt();
        var width = source.readInt();
        var height = source.readInt();
        var size = source.readInt();
        var data = source.readBytes(size);

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
