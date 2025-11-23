package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;

import java.io.*;

public record GtsLevel(
    int width,
    int height,
    int offset
) {
    public static GtsLevel read(BinaryReader reader) throws IOException {
        int width = reader.readInt();
        int height = reader.readInt();
        int offset = reader.readLongAsInt();

        return new GtsLevel(
            width,
            height,
            offset
        );
    }
}
