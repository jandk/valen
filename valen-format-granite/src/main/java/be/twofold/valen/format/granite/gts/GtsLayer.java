package be.twofold.valen.format.granite.gts;

import be.twofold.valen.format.granite.enums.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record GtsLayer(
    DataType type,
    int color
) {
    public static GtsLayer read(BinarySource source) throws IOException {
        var type = DataType.fromValue(source.readInt());
        var color = source.readInt();

        return new GtsLayer(
            type,
            color
        );
    }
}
