package be.twofold.valen.game.gustav.reader.pak;

import be.twofold.valen.core.io.*;

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
    public static PakEntry read(BinaryReader reader) throws IOException {
        var name = reader.readString(256).trim();
        var offset = Integer.toUnsignedLong(reader.readInt()) | Short.toUnsignedLong(reader.readShort()) << 32;
        var archivePart = reader.readByte();
        var flags = Compression.fromValue(reader.readByte());
        var compressedSize = reader.readInt();
        var size = reader.readInt();

        return new PakEntry(name, offset, archivePart, flags, compressedSize, size);
    }
}
