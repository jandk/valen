package be.twofold.valen.game.gustav.reader.pak;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record Pak(
    PakHeader header,
    List<PakEntry> entries
) {
    public static Pak read(BinaryReader reader) throws IOException {
        var header = PakHeader.read(reader);
        reader.position(header.fileListOffset());
        var entries = readEntries(reader);

        return new Pak(
            header,
            entries
        );
    }

    private static List<PakEntry> readEntries(BinaryReader reader) throws IOException {
        var numFiles = reader.readInt();
        var compressedSize = reader.readInt();
        var compressed = reader.readBytes(compressedSize);
        var decompressed = Decompressor.lz4().decompress(compressed, numFiles * 272);

        try (var entryReader = BinaryReader.fromBytes(decompressed)) {
            return entryReader.readObjects(numFiles, PakEntry::read);
        }
    }
}
