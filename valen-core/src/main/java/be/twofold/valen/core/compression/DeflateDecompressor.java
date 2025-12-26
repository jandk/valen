package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.zip.*;

final class DeflateDecompressor implements Decompressor {
    private final boolean nowrap;

    DeflateDecompressor(boolean nowrap) {
        this.nowrap = nowrap;
    }

    @Override
    public void decompress(Bytes src, Bytes.Mutable dst) throws IOException {
        var srcBuffer = src.asBuffer();
        var dstBuffer = dst.asMutableBuffer();

        try (var inflater = new Inflater(nowrap)) {
            inflater.setInput(srcBuffer);

            while (!inflater.finished()) {
                try {
                    int count = inflater.inflate(dstBuffer);
                    if (count == 0) {
                        break;
                    }
                } catch (DataFormatException e) {
                    throw new IOException("Invalid compressed data", e);
                }
            }
        }
    }
}
