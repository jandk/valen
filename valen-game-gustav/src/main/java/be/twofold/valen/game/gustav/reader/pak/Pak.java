package be.twofold.valen.game.gustav.reader.pak;

import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Pak(
    PakHeader header,
    List<PakEntry> entries
) {
    public static Pak read(BinarySource source) throws IOException {
        var header = PakHeader.read(source);
        source.position(header.fileListOffset());
        var entries = readEntries(source);

        return new Pak(
            header,
            entries
        );
    }

    private static List<PakEntry> readEntries(BinarySource source) throws IOException {
        var numFiles = source.readInt();
        var compressedSize = source.readInt();
        var compressed = source.readBytes(compressedSize);
        var decompressed = Decompressor.lz4Block().decompress(compressed, numFiles * 272);

        try (var entrySource = BinarySource.wrap(decompressed)) {
            return entrySource.readObjects(numFiles, PakEntry::read);
        }
    }
}
