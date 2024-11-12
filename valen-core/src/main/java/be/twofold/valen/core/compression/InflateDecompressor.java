package be.twofold.valen.core.compression;

import java.io.*;
import java.util.zip.*;

final class InflateDecompressor extends Decompressor {
    private final boolean nowrap;

    InflateDecompressor(boolean nowrap) {
        this.nowrap = nowrap;
    }

    @Override
    public void decompress(
        byte[] source, int sourceOffset, int sourceLength,
        byte[] target, int targetOffset, int targetLength
    ) throws IOException {
        var inflater = new Inflater(nowrap);
        inflater.setInput(source, sourceOffset, sourceLength);

        while (!inflater.finished()) {
            try {
                int count = inflater.inflate(target, targetOffset, targetLength);
                if (count == 0) {
                    break;
                }
                targetOffset += count;
                targetLength -= count;
            } catch (DataFormatException e) {
                throw new IOException("Invalid compressed data", e);
            }
        }
        inflater.end();
    }
}
