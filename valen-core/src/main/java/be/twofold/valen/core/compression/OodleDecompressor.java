package be.twofold.valen.core.compression;

import be.twofold.valen.core.compression.oodle.*;

import java.io.*;
import java.nio.*;

public final class OodleDecompressor implements Decompressor {
    private final boolean chunked;

    public OodleDecompressor(boolean chunked) {
        this.chunked = chunked;
    }

    @Override
    public void decompress(
        byte[] src, int srcOff, int srcLen,
        byte[] dst, int dstOff, int dstLen
    ) throws IOException {
        var sourceBuffer = ByteBuffer.wrap(src, srcOff, srcLen);
        var targetBuffer = ByteBuffer.wrap(dst, dstOff, dstLen);
        if (chunked) {
            sourceBuffer = sourceBuffer.slice(12, sourceBuffer.remaining() - 12);
        }

        Oodle.instance().decompress(sourceBuffer, targetBuffer);
    }
}
