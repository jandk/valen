package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Color3(
    byte r,
    byte g,
    byte b
) {
    public static final int BYTES = 3;

    public static Color3 read(DataSource source) throws IOException {
        byte r = source.readByte();
        byte g = source.readByte();
        byte b = source.readByte();
        return new Color3(r, g, b);
    }
}
