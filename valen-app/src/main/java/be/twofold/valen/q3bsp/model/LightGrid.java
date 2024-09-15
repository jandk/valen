package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;

import java.io.*;

public record LightGrid(
    Color3 ambient,
    Color3 directional,
    byte phi,
    byte theta
) {
    public static final int BYTES = 2 * Color3.BYTES + 2 * Byte.BYTES;

    public static LightGrid read(DataSource source) throws IOException {
        var ambient = Color3.read(source);
        var directional = Color3.read(source);
        byte phi = source.readByte();
        byte theta = source.readByte();

        return new LightGrid(ambient, directional, phi, theta);
    }
}
