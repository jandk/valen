package be.twofold.valen.game.darkages.reader.geometry;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.util.*;

public record GeometryDiskLayout(
    int compression,
    int uncompressedSize,
    int compressedSize,
    int offset
) implements GeoDiskLayout {
    public static GeometryDiskLayout read(BinarySource source) throws IOException {
        var compression = source.readInt();
        var uncompressedSize = source.readInt();
        var compressedSize = source.readInt();
        var offset = source.readInt();

        return new GeometryDiskLayout(
            compression,
            uncompressedSize,
            compressedSize,
            offset
        );
    }

    @Override
    public List<? extends GeoMemoryLayout> memoryLayouts() {
        return List.of();
    }
}
