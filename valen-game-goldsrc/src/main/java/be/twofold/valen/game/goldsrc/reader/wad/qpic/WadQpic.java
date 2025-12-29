package be.twofold.valen.game.goldsrc.reader.wad.qpic;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record WadQpic(
    int width,
    int height,
    Bytes data,
    Bytes palette
) {
    public static WadQpic read(BinarySource source) throws IOException {
        var width = source.readInt();
        var height = source.readInt();
        var data = source.readBytes(width * height);
        var paletteSize = source.readShort();
        var palette = source.readBytes(paletteSize * 3);

        return new WadQpic(
            width,
            height,
            data,
            palette
        );
    }
}
