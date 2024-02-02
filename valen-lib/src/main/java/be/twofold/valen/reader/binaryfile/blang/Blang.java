package be.twofold.valen.reader.binaryfile.blang;

import be.twofold.valen.core.util.*;

import java.util.*;

public record Blang(
    int versionMaybe,
    int unknownHash,
    int numEntries,
    List<BlangEntry> entries
) {
    // TODO: Support the other blang format
    public static Blang read(BetterBuffer buffer) {
        var versionMaybe = buffer.getInt();
        var unknownHash = buffer.getInt();
        var numEntries = Integer.reverseBytes(buffer.getInt()); // Big endian all of a sudden?
        var entries = buffer.getStructs(numEntries, BlangEntry::read);
        return new Blang(versionMaybe, unknownHash, numEntries, entries);
    }
}
