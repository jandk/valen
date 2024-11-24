package be.twofold.valen.core.compression;

import java.io.*;

public interface Decompressor {
    void decompress(
        byte[] src, int srcOff, int srcLen,
        byte[] dst, int dstOff, int dstLen
    ) throws IOException;
}
