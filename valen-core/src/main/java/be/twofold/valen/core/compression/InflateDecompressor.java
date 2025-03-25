package be.twofold.valen.core.compression;

import java.io.*;
import java.nio.*;
import java.util.zip.*;

final class InflateDecompressor implements Decompressor {
    private final boolean raw;

    InflateDecompressor(boolean raw) {
        this.raw = raw;
    }

    @Override
    public void decompress(ByteBuffer src, ByteBuffer dst) throws IOException {
        var inflater = new Inflater(raw);
        inflater.setInput(src);

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
    }
}
