package be.twofold.valen.format.granite.gts;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record GtsTile(
    short fileIndex,
    short pageIndex,
    short chunkIndex,
    short tileCount,
    int tileOffset
) {
    public static GtsTile read(BinarySource source) throws IOException {
        var fileIndex = source.readShort();
        var pageIndex = source.readShort();
        var chunkIndex = source.readShort();
        var tileCount = source.readShort();
        var tileOffset = source.readInt();

        return new GtsTile(
            fileIndex,
            pageIndex,
            chunkIndex,
            tileCount,
            tileOffset
        );
    }
}
