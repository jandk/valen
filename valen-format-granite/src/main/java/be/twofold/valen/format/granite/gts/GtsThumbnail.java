package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;
import be.twofold.valen.format.granite.util.*;

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
    public static GtsThumbnail read(BinaryReader reader) throws IOException {
        var guid = DotNetUtils.guidBytesToUUID(reader.readBytes(16));
        var offset = reader.readLongAsInt();
        var rawSize = reader.readInt();
        var codedSize = reader.readInt();
        var width = reader.readShort();
        var height = reader.readShort();

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
