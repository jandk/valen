package be.twofold.valen.middleware.wwise.codebook;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Codebook(
    int bitCount,
    Bytes bytes
) {
    public static Codebook read(BinarySource source) throws IOException {
        var bitCount = Short.toUnsignedInt(source.readShort());
        var bytes = source.readBytes(Math.ceilDiv(bitCount, 8));

        return new Codebook(
            bitCount,
            bytes
        );
    }
}
