package be.twofold.valen.game.greatcircle.reader.streamdb;

import be.twofold.valen.core.io.*;

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
    byte[] entriesTablesHash,
    byte[] entryHashesHash,
    byte[] headerHash
) {
    public static StreamDbHeader read(BinaryReader reader) throws IOException {
        reader.expectLong(0x61C7F32E29C2A551L);
        int headerLengthOrPad = reader.readInt();
        int version = reader.readInt();
        int pad0 = reader.readInt();
        int subBlockTableSize = reader.readInt();
        int numEntries = reader.readInt();
        int flags = reader.readInt();

        int hashTypeOrPad = reader.readInt();
        int entryHashesListOffset = reader.readInt();
        byte[] entriesTablesHash = reader.readBytes(32);
        byte[] entryHashesHash = reader.readBytes(32);
        byte[] headerHash = reader.readBytes(32);

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
