package be.twofold.valen.core.compression;

import java.io.*;
import java.nio.*;
import java.util.zip.*;

public final class InflateDecompressor extends Decompressor {
    private final boolean nowrap;

    public InflateDecompressor(boolean nowrap) {
        this.nowrap = nowrap;
    }

    @Override
    public ByteBuffer decompress(ByteBuffer src, int dstLength) throws IOException {
        var inflater = new Inflater(nowrap);
        inflater.setInput(src);
        var dst = ByteBuffer.allocate(dstLength);
        while (!inflater.finished()) {
            try {
                int count = inflater.inflate(dst);
                if (count == 0) {
                    break;
                }
            } catch (DataFormatException e) {
                throw new IOException("Invalid compressed data", e);
            }
        }
        inflater.end();
        return dst.flip();
    }
}
