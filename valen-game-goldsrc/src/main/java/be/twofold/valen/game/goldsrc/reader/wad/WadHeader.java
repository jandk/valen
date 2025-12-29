package be.twofold.valen.game.goldsrc.reader.wad;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record WadHeader(
    int entryCount,
    int entryOffset
) {
    public static WadHeader read(BinarySource source) throws IOException {
        source.expectInt(0x33444157); // "WAD3"
        var entryCount = source.readInt();
        var entryOffset = source.readInt();
        return new WadHeader(entryCount, entryOffset);
    }
}
