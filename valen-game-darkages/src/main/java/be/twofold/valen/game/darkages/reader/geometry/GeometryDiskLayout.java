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
    public static GeometryDiskLayout read(BinaryReader reader) throws IOException {
        var compression = reader.readInt();
        var uncompressedSize = reader.readInt();
        var compressedSize = reader.readInt();
        var offset = reader.readInt();

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
