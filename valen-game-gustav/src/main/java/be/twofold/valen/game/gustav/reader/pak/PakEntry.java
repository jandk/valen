package be.twofold.valen.game.gustav.reader.pak;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record PakEntry(
    String name,
    long offset,
    byte archivePart,
    Set<Compression> flags,
    int compressedSize,
    int size
) {
    public static PakEntry read(BinarySource source) throws IOException {
        var name = source.readString(256).trim();
        var offset = Integer.toUnsignedLong(source.readInt()) | Short.toUnsignedLong(source.readShort()) << 32;
        var archivePart = source.readByte();
        var flags = Compression.fromValue(source.readByte());
        var compressedSize = source.readInt();
        var size = source.readInt();

        return new PakEntry(name, offset, archivePart, flags, compressedSize, size);
    }
}
