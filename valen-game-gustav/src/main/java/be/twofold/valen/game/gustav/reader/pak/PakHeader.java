package be.twofold.valen.game.gustav.reader.pak;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.*;

public record PakHeader(
    long fileListOffset,
    int fileListLength,
    Set<PakFlag> flags,
    byte priority,
    Bytes md5,
    short numParts
) {
    public static PakHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x4B50534C); // magic
        reader.expectInt(18); // version
        var fileListOffset = reader.readLong();
        var fileListLength = reader.readInt();
        var flags = PakFlag.fromValue(reader.readByte());
        var priority = reader.readByte();
        var md5 = reader.readBytes(16);
        var numParts = reader.readShort();

        return new PakHeader(
            fileListOffset,
            fileListLength,
            flags,
            priority,
            md5,
            numParts
        );
    }
}
