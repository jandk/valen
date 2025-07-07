package be.twofold.valen.core.compression;

import java.io.*;
import java.nio.*;
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

    static Decompressor ooz(Path path) {
        return new OozDecompressor(path);
    }

    default ByteBuffer decompress(ByteBuffer src, int size) throws IOException {
        var dst = ByteBuffer.allocate(size);
        decompress(src, dst);
        dst.flip();
        return dst;
    }

    void decompress(ByteBuffer src, ByteBuffer dst) throws IOException;
}
