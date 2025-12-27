package be.twofold.valen.format.granite.gts;

import be.twofold.valen.format.granite.enums.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record GtsParamBlock(
    int id,
    Codec codec,
    int size,
    int offset
) {
    public static GtsParamBlock read(BinarySource source) throws IOException {
        var id = source.readInt();
        var codec = Codec.fromValue(source.readInt());
        var size = source.readInt();
        var offset = source.readInt();
        source.expectInt(0);

        return new GtsParamBlock(
            id,
            codec,
            size,
            offset
        );
    }
}
