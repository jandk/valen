package be.twofold.valen.game.colossus.reader.image;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record ImageTile(
    int sizeCompressed,
    int size,
    short x,
    short y,
    short width,
    short height,
    byte versionMaybe,
    byte[] coefficients,
    byte[] data
) {
    public static ImageTile read(DataSource source) throws IOException {
        int sizeCompressed = source.readInt();
        int size = source.readInt();
        short x = source.readShort();
        short y = source.readShort();
        short width = source.readShort();
        short height = source.readShort();
        byte versionMaybe = source.readByte();
        byte[] coefficients = source.readBytes(7);
        byte[] data = source.readBytes(sizeCompressed);

        return new ImageTile(
            sizeCompressed,
            size,
            x,
            y,
            width,
            height,
            versionMaybe,
            coefficients,
            data
        );
    }

    @Override
    public String toString() {
        return "ImageTile{" +
            "sizeCompressed=" + sizeCompressed +
            ", size=" + size +
            ", x=" + x +
            ", y=" + y +
            ", width=" + width +
            ", height=" + height +
            ", versionMaybe=" + versionMaybe +
            ", coefficients=" + Arrays.toString(coefficients) +
            // ", data=" + Arrays.toString(data) +
            '}';
    }
}
