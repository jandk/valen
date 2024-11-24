package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.*;

import java.io.*;

final class NoneDecompressor implements Decompressor {
    @Override
    public void decompress(
        byte[] src, int srcOff, int srcLen,
        byte[] dst, int dstOff, int dstLen
    ) throws IOException {
        Check.fromIndexSize(srcOff, srcLen, src.length);
        Check.fromIndexSize(dstOff, dstLen, dst.length);

        if (srcLen != dstLen) {
            throw new IOException("Invalid decompressed size");
        }
    }
}
