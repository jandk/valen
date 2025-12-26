package be.twofold.valen.game.greatcircle.reader.streamdb;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;

public record StreamDbHeader(
    // streamDatabaseMainHeader_t
    int headerLengthOrPad,
    int version,
    int pad0,
    int subBlockTableSize,
    int numEntries,
    int flags,

    // streamDatabaseHeaderHashes_t
    int hashTypeOrPad,
    int entryHashesListOffset,
    Bytes entriesTablesHash,
    Bytes entryHashesHash,
    Bytes headerHash
) {
    public static StreamDbHeader read(BinarySource source) throws IOException {
        source.expectLong(0x61C7F32E29C2A551L);
        int headerLengthOrPad = source.readInt();
        int version = source.readInt();
        int pad0 = source.readInt();
        int subBlockTableSize = source.readInt();
        int numEntries = source.readInt();
        int flags = source.readInt();

        int hashTypeOrPad = source.readInt();
        int entryHashesListOffset = source.readInt();
        Bytes entriesTablesHash = source.readBytes(32);
        Bytes entryHashesHash = source.readBytes(32);
        Bytes headerHash = source.readBytes(32);

        return new StreamDbHeader(
            headerLengthOrPad,
            version,
            pad0,
            subBlockTableSize,
            numEntries,
            flags,
            hashTypeOrPad,
            entryHashesListOffset,
            entriesTablesHash,
            entryHashesHash,
            headerHash
        );
    }
}
