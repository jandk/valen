package be.twofold.valen.format.granite.gts;

import be.twofold.valen.format.granite.util.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record GtsThumbnail(
    UUID guid,
    int offset,
    int rawSize,
    int codedSize,
    short width,
    short height
) {
    public static GtsThumbnail read(BinarySource source) throws IOException {
        var guid = DotNetUtils.guidBytesToUUID(source.readBytes(16));
        var offset = source.readLongAsInt();
        var rawSize = source.readInt();
        var codedSize = source.readInt();
        var width = source.readShort();
        var height = source.readShort();

        return new GtsThumbnail(
            guid,
            offset,
            rawSize,
            codedSize,
            width,
            height
        );
    }
}
