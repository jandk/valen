package be.twofold.valen.q3bsp;

import be.twofold.valen.core.io.*;

import java.io.*;

record Q3BspLump(
    int offset,
    int length
) {
    public static final int BYTES = 4 + 4;

    public static Q3BspLump read(DataSource source) throws IOException {
        var offset = source.readInt();
        var length = source.readInt();
        return new Q3BspLump(offset, length);
    }
}
