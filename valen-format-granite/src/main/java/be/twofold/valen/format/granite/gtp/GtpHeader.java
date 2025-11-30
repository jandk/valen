package be.twofold.valen.format.granite.gtp;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;

record GtpHeader(
    Bytes md5Sum
) {
    static final int BYTES = 24;

    static GtpHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x50415247); // magic
        reader.expectInt(4); // version
        var md5Sum = reader.readBytesStruct(16);
        return new GtpHeader(md5Sum);
    }
}
