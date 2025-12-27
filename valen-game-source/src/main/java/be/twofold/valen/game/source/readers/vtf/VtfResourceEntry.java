package be.twofold.valen.game.source.readers.vtf;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record VtfResourceEntry(
    VtfResourceTag tag,
    byte flags,
    int offset
) {
    public static VtfResourceEntry read(BinarySource source) throws IOException {
        var tag = VtfResourceTag.fromValue(source.readBytes(3));
        var flags = source.readByte();
        var offset = source.readInt();
        return new VtfResourceEntry(tag, flags, offset);
    }

    public boolean hasData() {
        return (flags & 0x02) == 0;
    }
}
