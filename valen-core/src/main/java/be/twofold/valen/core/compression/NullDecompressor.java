package be.twofold.valen.core.compression;

import java.io.*;
import java.nio.*;

final class NullDecompressor extends Decompressor {
    @Override
    public ByteBuffer decompress(ByteBuffer src, int dstLength) throws IOException {
        if (src.remaining() != dstLength) {
            throw new IOException("Invalid decompressed size");
        }
        return src;
    }
}
