package be.twofold.valen.game.eternal.reader.binaryfile.blang;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
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
        var numEntries = reader.order(ByteOrder.BIG_ENDIAN).readInt(); // Big endian all of a sudden?
        var entries = reader.order(ByteOrder.LITTLE_ENDIAN).readObjects(numEntries, BlangEntry::read);
        return new Blang(versionMaybe, unknownHash, numEntries, entries);
    }
}
