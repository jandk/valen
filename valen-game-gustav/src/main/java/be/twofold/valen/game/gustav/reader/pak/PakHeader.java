package be.twofold.valen.game.gustav.reader.pak;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

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
    public static PakHeader read(BinarySource source) throws IOException {
        source.expectInt(0x4B50534C); // magic
        source.expectInt(18); // version
        var fileListOffset = source.readLong();
        var fileListLength = source.readInt();
        var flags = PakFlag.fromValue(source.readByte());
        var priority = source.readByte();
        var md5 = source.readBytes(16);
        var numParts = source.readShort();

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
