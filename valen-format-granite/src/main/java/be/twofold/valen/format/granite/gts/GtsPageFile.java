package be.twofold.valen.format.granite.gts;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.charset.*;

public record GtsPageFile(
    String filename,
    int pageCount,
    Bytes checksum,
    int type,
    int size
) {
    public static GtsPageFile read(BinarySource source, int version) throws IOException {
        var filename = source.readString(0x200, StandardCharsets.UTF_16LE).trim();
        var pageCount = source.readInt();
        var checksum = source.readBytes(16);
        var type = source.readInt();
        int size = version > 5 ? source.readLongAsInt() : 0;

        return new GtsPageFile(
            filename,
            pageCount,
            checksum,
            type,
            size
        );
    }
}
