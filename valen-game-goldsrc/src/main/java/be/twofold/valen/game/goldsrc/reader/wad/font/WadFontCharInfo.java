package be.twofold.valen.game.goldsrc.reader.wad.font;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record WadFontCharInfo(
    byte offsetX,
    byte offsetY,
    short width
) {
    public static WadFontCharInfo read(BinarySource source) throws IOException {
        var offsetX = source.readByte();
        var offsetY = source.readByte();
        var width = source.readShort();

        return new WadFontCharInfo(
            offsetX,
            offsetY,
            width
        );
    }
}
