package be.twofold.valen.game.colossus.reader.image;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ImageTile(
    int sizeCompressed,
    int size,
    short x,
    short y,
    short width,
    short height,
    byte format,
    Bytes scales,
    Bytes data
) {
    public static ImageTile read(BinarySource source) throws IOException {
        int sizeCompressed = source.readInt();
        int size = source.readInt();
        short x = source.readShort();
        short y = source.readShort();
        short width = source.readShort();
        short height = source.readShort();
        byte format = source.readByte();
        Bytes scales = source.readBytes(7);
        Bytes data = source.readBytes(sizeCompressed);

        return new ImageTile(
            sizeCompressed,
            size,
            x,
            y,
            width,
            height,
            format,
            scales,
            data
        );
    }
}
