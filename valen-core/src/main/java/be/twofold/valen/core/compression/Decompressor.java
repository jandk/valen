package be.twofold.valen.core.compression;

import java.io.*;
import java.nio.file.*;

@FunctionalInterface
public interface Decompressor {
    static Decompressor none() {
        return new NoneDecompressor();
    }

    static Decompressor fastLZ() {
        return new FastLZDecompressor();
    }

    static Decompressor inflate(boolean raw) {
        return new InflateDecompressor(raw);
    }

    static Decompressor lz4() {
        return new LZ4Decompressor();
    }

    static Decompressor oodle(Path path) {
        return new OodleDecompressor(path);
    }

    default byte[] decompress(byte[] compressed, int uncompressedSize) throws IOException {
        byte[] decompressed = new byte[uncompressedSize];
        decompress(
            compressed, 0, compressed.length,
            decompressed, 0, decompressed.length
        );
        return decompressed;
    }

    void decompress(
        byte[] src, int srcOff, int srcLen,
        byte[] dst, int dstOff, int dstLen
    ) throws IOException;
}
