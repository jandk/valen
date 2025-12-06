package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.charset.*;

public record GtsPageFile(
    String filename,
    int pageCount,
    Bytes checksum,
    int type,
    int size
) {
    public static GtsPageFile read(BinaryReader reader, int version) throws IOException {
        var filename = reader.readString(0x200, StandardCharsets.UTF_16LE).trim();
        var pageCount = reader.readInt();
        var checksum = reader.readBytes(16);
        var type = reader.readInt();
        int size = version > 5 ? reader.readLongAsInt() : 0;

        return new GtsPageFile(
            filename,
            pageCount,
            checksum,
            type,
            size
        );
    }
}
