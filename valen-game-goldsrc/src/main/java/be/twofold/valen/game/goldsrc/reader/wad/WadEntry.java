package be.twofold.valen.game.goldsrc.reader.wad;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record WadEntry(
    int offset,
    int sizeCompressed,
    int size,
    WadEntryType type,
    byte compression,
    String name
) {
    public static WadEntry read(BinarySource source) throws IOException {
        var offset = source.readInt();
        var sizeCompressed = source.readInt();
        var size = source.readInt();
        var type = WadEntryType.fromValue(source.readByte());
        var compression = source.readByte();
        source.expectShort((short) 0);
        var name = source.readString(16);
        var index = name.indexOf('\0');
        name = index < 0 ? name : name.substring(0, index);

        return new WadEntry(
            offset,
            sizeCompressed,
            size,
            type,
            compression,
            name
        );
    }
}
