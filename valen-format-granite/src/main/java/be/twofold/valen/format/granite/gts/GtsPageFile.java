package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.charset.*;

public record GtsPageFile(
    String filename,
    int pageCount,
    Bytes checksum,
    int type
) {
    public static GtsPageFile read(BinaryReader reader) throws IOException {
        var filename = reader.readString(0x200, StandardCharsets.UTF_16LE).trim();
        var pageCount = reader.readInt();
        var checksum = reader.readBytesStruct(16);
        var type = reader.readInt();

        return new GtsPageFile(
            filename,
            pageCount,
            checksum,
            type
        );
    }
}
