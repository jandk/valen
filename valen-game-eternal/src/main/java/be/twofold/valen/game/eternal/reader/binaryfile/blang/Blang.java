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
    public static Blang read(BinaryReader reader) throws IOException {
        var versionMaybe = reader.readInt();
        var unknownHash = reader.readInt();
        var numEntries = reader.readIntBE(); // Big endian all of a sudden?
        var entries = reader.readObjects(numEntries, BlangEntry::read);
        return new Blang(versionMaybe, unknownHash, numEntries, entries);
    }
}
