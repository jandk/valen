package be.twofold.valen.compression;

import java.io.*;
import java.nio.*;

public final class NullDecompressor implements Decompressor {
    @Override
    public ByteBuffer decompress(ByteBuffer src, int dstLength) throws IOException {
        if (src.remaining() != dstLength) {
            throw new IOException("Invalid decompressed size");
        }
        return src;
    }
}
