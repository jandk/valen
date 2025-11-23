package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;

import java.io.*;

public record GtsTile(
    short fileIndex,
    short pageIndex,
    short chunkIndex,
    short tileCount,
    int tileOffset
) {
    public static GtsTile read(BinaryReader reader) throws IOException {
        var fileIndex = reader.readShort();
        var pageIndex = reader.readShort();
        var chunkIndex = reader.readShort();
        var tileCount = reader.readShort();
        var tileOffset = reader.readInt();

        return new GtsTile(
            fileIndex,
            pageIndex,
            chunkIndex,
            tileCount,
            tileOffset
        );
    }
}
