package be.twofold.valen.game.darkages.reader.streamdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StreamDbHeader(
    int headerLength,
    int numEntries,
    Set<StreamDbHeaderFlags> flags
) {
    public static StreamDbHeader read(BinarySource source) throws IOException {
        source.expectLong(0x61C7F32E29C2A550L); // magic
        var headerLength = source.readInt();
        source.expectInt(0x0); // pad0
        source.expectInt(0x0); // pad1
        source.expectInt(0x0); // pad2
        var numEntries = source.readInt();
        var flags = StreamDbHeaderFlags.read(source);

        return new StreamDbHeader(
            headerLength,
            numEntries,
            flags
        );
    }
}
