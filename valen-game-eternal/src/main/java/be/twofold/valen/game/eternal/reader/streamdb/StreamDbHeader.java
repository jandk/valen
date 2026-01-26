package be.twofold.valen.game.eternal.reader.streamdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StreamDbHeader(
    long magic,
    int headerLength,
    int pad0,
    int pad1,
    int pad2,
    int numEntries,
    Set<StreamDbHeaderFlags> flags
) {
    public static StreamDbHeader read(BinarySource source) throws IOException {
        var magic = source.readLong();
        var headerLength = source.readInt();
        var pad0 = source.readInt();
        var pad1 = source.readInt();
        var pad2 = source.readInt();
        var numEntries = source.readInt();
        var flags = StreamDbHeaderFlags.read(source);

        return new StreamDbHeader(
            magic,
            headerLength,
            pad0,
            pad1,
            pad2,
            numEntries,
            flags
        );
    }
}
