package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record GeometryDiskLayout(
    int compression,
    int uncompressedSize,
    int compressedSize,
    int offset,
    List<GeometryMemoryLayout> memoryLayouts
) {
    public static GeometryDiskLayout read(DataSource source, List<GeometryMemoryLayout> memoryLayouts) throws IOException {
        var compression = source.readInt();
        var uncompressedSize = source.readInt();
        var compressedSize = source.readInt();
        var offset = source.readInt();

        return new GeometryDiskLayout(
            compression,
            uncompressedSize,
            compressedSize,
            offset,
            memoryLayouts
        );
    }
}
