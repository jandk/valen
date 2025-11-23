package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;

import java.io.*;

public record GtsLayer(
    int type,
    int color
) {
    public static GtsLayer read(BinaryReader reader) throws IOException {
        int type = reader.readInt();
        int color = reader.readInt();

        return new GtsLayer(
            type,
            color
        );
    }
}
