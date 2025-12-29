package be.twofold.valen.game.goldsrc.reader.wad.font;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record WadFont(
    int width,
    int height,
    int rowCount,
    int rowHeight,
    List<WadFontCharInfo> fontInfo,
    Bytes data,
    Bytes palette
) {
    public static WadFont read(BinarySource source) throws IOException {
        var width = source.readInt();
        var height = source.readInt();
        var rowCount = source.readInt();
        var rowHeight = source.readInt();
        var fontInfo = source.readObjects(256, WadFontCharInfo::read);
        var data = source.readBytes(256 * height);
        var paletteSize = source.readShort();
        var palette = source.readBytes(paletteSize);

        return new WadFont(
            width,
            height,
            rowCount,
            rowHeight,
            fontInfo,
            data,
            palette
        );
    }
}
