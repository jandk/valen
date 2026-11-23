package be.twofold.valen.game.darkages.reader.streamdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StreamDbHeader(
    int headerLength,
    int numEntries,
    Set<StreamDbHeaderFlags> flags
) {
    public static StreamDbHeader read(BinaryReader reader) throws IOException {
        reader.expectLong(0x61C7F32E29C2A550L); // magic
        var headerLength = reader.readInt();
        reader.expectInt(0x0); // pad0
        reader.expectInt(0x0); // pad1
        reader.expectInt(0x0); // pad2
        var numEntries = reader.readInt();
        var flags = StreamDbHeaderFlags.read(reader);

        return new StreamDbHeader(
            headerLength,
            numEntries,
            flags
        );
    }
}
