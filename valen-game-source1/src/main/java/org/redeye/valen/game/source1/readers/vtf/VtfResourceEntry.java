package org.redeye.valen.game.source1.readers.vtf;

import be.twofold.valen.core.io.*;

import java.io.*;

public record VtfResourceEntry(
    VtfResourceTag tag,
    byte flags,
    int offset
) {
    public static VtfResourceEntry read(DataSource source) throws IOException {
        var tag = VtfResourceTag.fromValue(source.readBytes(3));
        var flags = source.readByte();
        var offset = source.readInt();
        return new VtfResourceEntry(tag, flags, offset);
    }

    public boolean hasData() {
        return (flags & 0x02) == 0;
    }
}
