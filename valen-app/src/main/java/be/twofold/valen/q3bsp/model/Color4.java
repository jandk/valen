package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Color4(
    byte r,
    byte g,
    byte b,
    byte a
) {
    public static final int BYTES = 4;

    public static Color4 read(DataSource source) throws IOException {
        byte r = source.readByte();
        byte g = source.readByte();
        byte b = source.readByte();
        byte a = source.readByte();
        return new Color4(r, g, b, a);
    }
}
