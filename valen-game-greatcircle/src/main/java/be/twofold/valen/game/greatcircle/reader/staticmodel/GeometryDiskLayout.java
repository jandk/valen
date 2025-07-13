package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record GeometryDiskLayout(
    int compression,
    int uncompressedSize,
    int compressedSize,
    int offset,
    long hash,
    List<GeometryMemoryLayout> memoryLayouts
) {
    public static GeometryDiskLayout read(BinaryReader reader, List<GeometryMemoryLayout> memoryLayouts, int version) throws IOException {
        var compression = reader.readInt();
        var uncompressedSize = reader.readInt();
        var compressedSize = reader.readInt();
        var offset = reader.readInt();
        var hash = version > 78 ? reader.readLong() : 0;

        return new GeometryDiskLayout(
            compression,
            uncompressedSize,
            compressedSize,
            offset,
            hash,
            memoryLayouts
        );
    }
}
