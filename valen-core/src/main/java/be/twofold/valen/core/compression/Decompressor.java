package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.file.*;

@FunctionalInterface
public interface Decompressor {
    static Decompressor none() {
        return NoneDecompressor.INSTANCE;
    }

    static Decompressor fastLZ() {
        return FastLZDecompressor.INSTANCE;
    }

    static Decompressor inflate(boolean raw) {
        return new InflateDecompressor(raw);
    }

    static Decompressor lz4() {
        return LZ4Decompressor.INSTANCE;
    }

    static Decompressor oodle() {
        return OodleDecompressor.download();
    }

    static Decompressor oodle(Path path) {
        return OodleDecompressor.load(path);
    }

    static Decompressor ooz(Path path) {
        return new OozDecompressor(path);
    }

    default Bytes decompress(Bytes src, int size) throws IOException {
        var dst = MutableBytes.allocate(size);
        decompress(src, dst);
        return dst;
    }

    void decompress(Bytes src, MutableBytes dst) throws IOException;
}
