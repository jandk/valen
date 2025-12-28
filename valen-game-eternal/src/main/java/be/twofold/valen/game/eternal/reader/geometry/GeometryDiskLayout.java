package be.twofold.valen.game.eternal.reader.geometry;

import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record GeometryDiskLayout(
    int compression,
    int uncompressedSize,
    int compressedSize,
    int offset,
    List<GeometryMemoryLayout> memoryLayouts
) implements GeoDiskLayout {
    public static GeometryDiskLayout read(BinarySource source, List<GeometryMemoryLayout> memoryLayouts) throws IOException {
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
