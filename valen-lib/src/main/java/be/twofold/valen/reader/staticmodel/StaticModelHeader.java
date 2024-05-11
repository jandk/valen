package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StaticModelHeader(
    int numMeshes,
    float unknown1,
    float unknown2,
    float unknown3,
    boolean streamed
) {
    public static StaticModelHeader read(DataSource source) throws IOException {
        source.expectInt(0);
        source.expectInt(0);
        source.expectInt(0);
        source.expectInt(5);
        var numMeshes = source.readInt();
        source.expectInt(0);
        var unknown1 = source.readFloat();
        var unknown2 = source.readFloat();
        var unknown3 = source.readFloat();
        source.expectInt(0);
        var streamed = source.readBoolInt();
        return new StaticModelHeader(
            numMeshes,
            unknown1,
            unknown2,
            unknown3,
            streamed
        );
    }
}
