package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;
import be.twofold.valen.format.granite.enums.*;

import java.io.*;

public record GtsParamBlock(
    int id,
    Codec codec,
    int size,
    int offset
) {
    public static GtsParamBlock read(BinaryReader reader) throws IOException {
        var id = reader.readInt();
        var codec = Codec.fromValue(reader.readInt());
        var size = reader.readInt();
        var offset = reader.readInt();
        reader.expectPadding(4);

        return new GtsParamBlock(
            id,
            codec,
            size,
            offset
        );
    }
}
