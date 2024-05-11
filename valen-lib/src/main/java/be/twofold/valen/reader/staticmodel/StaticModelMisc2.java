package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StaticModelMisc2(
    byte unknown,
    byte unknownBool
) {
    public static StaticModelMisc2 read(DataSource source) throws IOException {
        var unknown = source.readByte();
        var unknownBool = source.readByte();
        source.expectShort((short) 0);
        return new StaticModelMisc2(unknown, unknownBool);
    }
}
