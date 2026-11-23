package be.twofold.valen.game.greatcircle.reader.streamdb;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record StreamDbHeader(
    int numEntries,
    Bytes entriesTablesHash,
    Bytes entryHashesHash,
    Bytes headerHash
) {
    public static StreamDbHeader read(BinaryReader reader) throws IOException {
        reader.expectLong(0x61C7F32E29C2A551L); // magic
        reader.expectInt(0x0); // headerLengthOrPad
        reader.expectInt(0x4); // version
        reader.expectInt(0x0); // pad0
        reader.expectInt(0x4); // subBlockTableSize
        var numEntries = reader.readInt();
        reader.expectInt(0x0); // flags
        reader.expectInt(0x1); // hashTypeOrPad
        reader.expectInt(0x0); // entryHashesListOffset
        var entriesTablesHash = reader.readBytes(32);
        var entryHashesHash = reader.readBytes(32);
        var headerHash = reader.readBytes(32);

        return new StreamDbHeader(
            numEntries,
            entriesTablesHash,
            entryHashesHash,
            headerHash
        );
    }
}
