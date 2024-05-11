package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StaticModelMisc1(
    float unkFloat,
    int unknown1,
    int unknown2
) {
    public static StaticModelMisc1 read(DataSource source) throws IOException {
        var unkFloat = source.readFloat();
        var unknown1 = source.readInt();
        var unknown2 = source.readInt();
        source.expectInt(0);
        return new StaticModelMisc1(unkFloat, unknown1, unknown2);
    }
}
