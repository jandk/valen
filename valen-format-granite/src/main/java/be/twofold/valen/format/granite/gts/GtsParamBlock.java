package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;

import java.io.*;

public record GtsParamBlock(
    int id,
    int codec,
    int size,
    int offset
) {
    public static GtsParamBlock read(BinaryReader reader) throws IOException {
        var id = reader.readInt();
        var codec = reader.readInt();
        var size = reader.readInt();
        var offset = reader.readInt();
        reader.expectInt(0);

        return new GtsParamBlock(
            id,
            codec,
            size,
            offset
        );
    }
}
