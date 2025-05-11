package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.util.*;

record GeometryDiskLayout(
    int compression,
    int uncompressedSize,
    int compressedSize,
    int offset,
    List<GeometryMemoryLayout> memoryLayouts
) implements GeoDiskLayout {
    static GeometryDiskLayout read(DataSource source, List<GeometryMemoryLayout> memoryLayouts) throws IOException {
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
