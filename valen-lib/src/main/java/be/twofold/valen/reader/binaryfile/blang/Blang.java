package be.twofold.valen.reader.binaryfile.blang;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record Blang(
    int versionMaybe,
    int unknownHash,
    int numEntries,
    List<BlangEntry> entries
) {
    public static Blang read(DataSource source) throws IOException {
        var versionMaybe = source.readInt();
        var unknownHash = source.readInt();
        var numEntries = Integer.reverseBytes(source.readInt()); // Big endian all of a sudden?
        var entries = source.readStructs(numEntries, BlangEntry::read);
        return new Blang(versionMaybe, unknownHash, numEntries, entries);
    }
}
