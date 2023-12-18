package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.util.*;

import java.util.*;

public record GeometryDiskLayout(
    int compression,
    int uncompressedSize,
    int compressedSize,
    int offset,
    List<GeometryMemoryLayout> memoryLayouts
) {
    public static GeometryDiskLayout read(BetterBuffer buffer, List<GeometryMemoryLayout> memoryLayouts) {
        var compression = buffer.getInt();
        var uncompressedSize = buffer.getInt();
        var compressedSize = buffer.getInt();
        var offset = buffer.getInt();

        return new GeometryDiskLayout(
            compression,
            uncompressedSize,
            compressedSize,
            offset,
            memoryLayouts
        );
    }
}
