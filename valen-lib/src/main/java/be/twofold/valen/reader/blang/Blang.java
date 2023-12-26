package be.twofold.valen.reader.blang;

import be.twofold.valen.core.util.*;

import java.util.*;

public record Blang(
    int numEntries,
    List<BlangEntry> entries
) {
    // TODO: Support the other blang format
    public static Blang read(BetterBuffer buffer) {
        // Big endian all of a sudden?
        var numEntries = Integer.reverseBytes(buffer.getInt());
        var entries = buffer.getStructs(numEntries, BlangEntry::read);
        return new Blang(numEntries, entries);
    }
}
