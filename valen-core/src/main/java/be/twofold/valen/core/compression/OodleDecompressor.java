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
    public void decompress(
        byte[] source, int sourceOffset, int sourceLength,
        byte[] target, int targetOffset, int targetLength
    ) throws IOException {
        var sourceBuffer = ByteBuffer.wrap(source, sourceOffset, sourceLength);
        var targetBuffer = ByteBuffer.wrap(target, targetOffset, targetLength);
        if (chunked) {
            sourceBuffer = sourceBuffer.slice(12, sourceBuffer.remaining() - 12);
        }

        Oodle.instance().decompress(sourceBuffer, targetBuffer);
    }
}
