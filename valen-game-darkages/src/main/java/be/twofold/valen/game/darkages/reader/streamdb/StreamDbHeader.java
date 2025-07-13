package be.twofold.valen.game.darkages.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StreamDbHeader(
    int length,
    int numEntries,
    Set<StreamDbHeaderFlag> flags
) {
    public static StreamDbHeader read(BinaryReader reader) throws IOException {
        reader.expectLong(0x61c7f32e29c2a550L); // magic
        var length = reader.readInt();
        reader.expectInt(0); // padding
        reader.expectInt(0); // padding
        reader.expectInt(0); // padding
        var numEntries = reader.readInt();
        var flags = StreamDbHeaderFlag.fromValue(reader.readInt());
        return new StreamDbHeader(length, numEntries, flags);
    }
}
