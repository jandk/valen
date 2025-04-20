package be.twofold.valen.game.eternal.reader.binaryfile.blang;

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
        var numEntries = source.readIntBE(); // Big endian all of a sudden?
        var entries = source.readObjects(numEntries, BlangEntry::read);
        return new Blang(versionMaybe, unknownHash, numEntries, entries);
    }
}
