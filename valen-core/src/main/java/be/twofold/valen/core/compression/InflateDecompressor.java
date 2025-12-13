package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.zip.*;

final class InflateDecompressor implements Decompressor {
    private final boolean raw;

    InflateDecompressor(boolean raw) {
        this.raw = raw;
    }

    @Override
    public void decompress(Bytes src, MutableBytes dst) throws IOException {
        try (var inflater = new Inflater(raw)) {
            inflater.setInput(src.asBuffer());

            while (!inflater.finished()) {
                try {
                    int count = inflater.inflate(dst.asMutableBuffer());
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
