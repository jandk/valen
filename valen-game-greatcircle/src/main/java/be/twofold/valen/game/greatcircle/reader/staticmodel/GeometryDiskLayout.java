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
    public static GeometryDiskLayout read(BinarySource source, List<GeometryMemoryLayout> memoryLayouts, int version) throws IOException {
        var compression = source.readInt();
        var uncompressedSize = source.readInt();
        var compressedSize = source.readInt();
        var offset = source.readInt();
        var hash = version > 78 ? source.readLong() : 0;

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
