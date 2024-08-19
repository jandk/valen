package be.twofold.valen.game.colossus.reader.image;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ImageTile(
    int sizeCompressed,
    int size,
    short x,
    short y,
    short width,
    short height,
    byte format,
    byte[] scales,
    byte[] data
) {
    public static ImageTile read(DataSource source) throws IOException {
        int sizeCompressed = source.readInt();
        int size = source.readInt();
        short x = source.readShort();
        short y = source.readShort();
        short width = source.readShort();
        short height = source.readShort();
        byte format = source.readByte();
        byte[] scales = source.readBytes(7);
        byte[] data = source.readBytes(sizeCompressed);

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
