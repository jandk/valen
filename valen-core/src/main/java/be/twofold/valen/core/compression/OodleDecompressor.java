package be.twofold.valen.core.compression;

import be.twofold.valen.core.compression.oodle.*;

import java.io.*;
import java.nio.*;

public final class OodleDecompressor extends Decompressor {
    private final boolean chunked;

    public OodleDecompressor(boolean chunked) {
        this.chunked = chunked;
    }

    @Override
    public ByteBuffer decompress(ByteBuffer src, int dstLength) throws IOException {
        if (chunked) {
            src = src.slice(12, src.remaining() - 12);
        }

        var dst = ByteBuffer.allocate(dstLength);
        Oodle.instance().decompress(src, dst);
        return dst;
    }
}
