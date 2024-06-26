package be.twofold.valen.reader.lightdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record LightDbPart1(
    int offset,
    int length,
    int unk3
) {
    public static final int BYTES = 16;

    public static LightDbPart1 read(DataSource source) throws IOException {
        var offset = source.readInt();
        source.expectInt(0);
        var length = source.readInt();
        var unk3 = source.readInt();

        return new LightDbPart1(
            offset,
            length,
            unk3
        );
    }
}
