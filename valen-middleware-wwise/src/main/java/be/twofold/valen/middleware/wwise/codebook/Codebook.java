package be.twofold.valen.middleware.wwise.codebook;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Codebook(
    int bitCount,
    Bytes bytes
) {
    public static Codebook read(BinaryReader reader) throws IOException {
        var bitCount = reader.readUnsignedShort();
        var bytes = reader.readBytes(Math.ceilDiv(bitCount, 8));

        return new Codebook(
            bitCount,
            bytes
        );
    }
}
