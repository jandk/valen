package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Fog(
    String shader,
    int brushNum,
    int visibleSide
) {
    public static final int BYTES = 64 + Integer.BYTES + Integer.BYTES;

    public static Fog read(DataSource source) throws IOException {
        String shader = new String(source.readBytes(64)).trim();
        int brushNum = source.readInt();
        int visibleSide = source.readInt();
        return new Fog(
            shader,
            brushNum,
            visibleSide
        );
    }
}
