package be.twofold.valen.core.compression;

import java.io.*;
import java.util.zip.*;

final class InflateDecompressor implements Decompressor {
    private final boolean nowrap;

    InflateDecompressor(boolean nowrap) {
        this.nowrap = nowrap;
    }

    @Override
    public void decompress(
        byte[] src, int srcOff, int srcLen,
        byte[] dst, int dstOff, int dstLen
    ) throws IOException {
        var inflater = new Inflater(nowrap);
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
