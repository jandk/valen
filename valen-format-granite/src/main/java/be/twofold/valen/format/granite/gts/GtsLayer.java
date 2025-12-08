package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;
import be.twofold.valen.format.granite.enums.*;

import java.io.*;

public record GtsLayer(
    DataType type,
    int color
) {
    public static GtsLayer read(BinaryReader reader) throws IOException {
        var type = DataType.fromValue(reader.readInt());
        var color = reader.readInt();

        return new GtsLayer(
            type,
            color
        );
    }
}
