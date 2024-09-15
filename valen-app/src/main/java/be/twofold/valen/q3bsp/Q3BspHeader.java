package be.twofold.valen.q3bsp;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

record Q3BspHeader(
    int ident,
    int version,
    List<Q3BspLump> lumps
) {
    public static final int IDENT = 0x50534249;
    public static final int VERSION = 46;
    public static final int Q3_HEADER_LUMPS = 17;
    public static final int BYTES = 4 + 4 + Q3_HEADER_LUMPS * Q3BspLump.BYTES;

    public static Q3BspHeader read(DataSource source) throws IOException {
        var ident = source.readInt();
        if (ident != IDENT) {
            throw new IllegalArgumentException("Not a Quake 3 BSP file");
        }

        var version = source.readInt();
        if (version != VERSION) {
            throw new IllegalArgumentException("Unsupported Quake 3 BSP version: " + version);
        }

        var lumps = source.readStructs(Q3_HEADER_LUMPS, Q3BspLump::read);

        return new Q3BspHeader(ident, version, lumps);
    }
}
