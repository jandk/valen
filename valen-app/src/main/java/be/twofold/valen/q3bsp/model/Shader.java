package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Shader(
    String shader,
    int surfaceFlags,
    int contentFlags
) {
    public static final int BYTES = 64 + Integer.BYTES + Integer.BYTES;

    public static Shader read(DataSource source) throws IOException {
        String shader = new String(source.readBytes(64)).trim();
        int surfaceFlags = source.readInt();
        int contentFlags = source.readInt();
        return new Shader(
            shader,
            surfaceFlags,
            contentFlags
        );
    }
}
