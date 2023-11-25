package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.util.*;

public record GeometryDiskLayout(
    int compression,
    int uncompressedSize,
    int compressedSize,
    int offset
) {
    public static GeometryDiskLayout read(BetterBuffer buffer) {
        int compression = buffer.getInt();
        int uncompressedSize = buffer.getInt();
        int compressedSize = buffer.getInt();
        int offset = buffer.getInt();

        return new GeometryDiskLayout(
            compression,
            uncompressedSize,
            compressedSize,
            offset
        );
    }
}
