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
    public static StreamDbHeader read(BinarySource source) throws IOException {
        source.expectLong(0x61C7F32E29C2A551L); // magic
        source.expectInt(0x0); // headerLengthOrPad
        source.expectInt(0x4); // version
        source.expectInt(0x0); // pad0
        source.expectInt(0x4); // subBlockTableSize
        var numEntries = source.readInt();
        source.expectInt(0x0); // flags
        source.expectInt(0x1); // hashTypeOrPad
        source.expectInt(0x0); // entryHashesListOffset
        var entriesTablesHash = source.readBytes(32);
        var entryHashesHash = source.readBytes(32);
        var headerHash = source.readBytes(32);

        return new StreamDbHeader(
            numEntries,
            entriesTablesHash,
            entryHashesHash,
            headerHash
        );
    }
}
