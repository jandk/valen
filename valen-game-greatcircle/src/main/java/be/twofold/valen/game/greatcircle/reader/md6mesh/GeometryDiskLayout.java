package be.twofold.valen.game.greatcircle.reader.md6mesh;

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
    static GeometryDiskLayout read(BinaryReader reader, List<GeometryMemoryLayout> memoryLayouts) throws IOException {
        var compression = reader.readInt();
        var uncompressedSize = reader.readInt();
        var compressedSize = reader.readInt();
        var offset = reader.readInt();

        return new GeometryDiskLayout(
            compression,
            uncompressedSize,
            compressedSize,
            offset,
            memoryLayouts
        );
    }
}
