package be.twofold.valen.format.granite.gtp;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

record GtpHeader(
    Bytes md5Sum
) {
    static final int BYTES = 24;

    static GtpHeader read(BinarySource source) throws IOException {
        source.expectInt(0x50415247); // magic
        source.expectInt(4); // version
        var md5Sum = source.readBytes(16);
        return new GtpHeader(md5Sum);
    }
}
