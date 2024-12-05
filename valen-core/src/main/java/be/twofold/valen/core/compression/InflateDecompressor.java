package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.util.zip.*;

final class InflateDecompressor implements Decompressor {
    private final boolean raw;

    InflateDecompressor(boolean raw) {
        this.raw = raw;
    }

    @Override
    public void decompress(
        byte[] src, int srcOff, int srcLen,
        byte[] dst, int dstOff, int dstLen
    ) throws IOException {
        Check.fromIndexSize(srcOff, srcLen, src.length);
        Check.fromIndexSize(dstOff, dstLen, dst.length);

        var inflater = new Inflater(raw);
        inflater.setInput(src, srcOff, srcLen);

        while (!inflater.finished()) {
            try {
                int count = inflater.inflate(dst, dstOff, dstLen);
                if (count == 0) {
                    break;
                }
                dstOff += count;
                dstLen -= count;
            } catch (DataFormatException e) {
                throw new IOException("Invalid compressed data", e);
            }
        }
        inflater.end();
    }
}
