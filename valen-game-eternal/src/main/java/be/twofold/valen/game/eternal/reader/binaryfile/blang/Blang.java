package be.twofold.valen.game.eternal.reader.binaryfile.blang;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record Blang(
    int versionMaybe,
    int unknownHash,
    int numEntries,
    List<BlangEntry> entries
) {
    public static Blang read(BinarySource source) throws IOException {
        var versionMaybe = source.readInt();
        var unknownHash = source.readInt();
        var numEntries = source.order(ByteOrder.BIG_ENDIAN).readInt(); // Big endian all of a sudden?
        var entries = source.order(ByteOrder.LITTLE_ENDIAN).readObjects(numEntries, BlangEntry::read);
        return new Blang(versionMaybe, unknownHash, numEntries, entries);
    }
}
