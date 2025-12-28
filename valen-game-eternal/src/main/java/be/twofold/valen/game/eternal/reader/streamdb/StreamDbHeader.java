package be.twofold.valen.game.eternal.reader.streamdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StreamDbHeader(
    int length,
    int numEntries,
    Set<StreamDbHeaderFlag> flags
) {
    public static StreamDbHeader read(BinarySource source) throws IOException {
        source.expectLong(0x61c7f32e29c2a550L); // magic
        var length = source.readInt();
        source.expectInt(0); // padding
        source.expectInt(0); // padding
        source.expectInt(0); // padding
        var numEntries = source.readInt();
        var flags = StreamDbHeaderFlag.fromValue(source.readInt());
        return new StreamDbHeader(length, numEntries, flags);
    }
}
