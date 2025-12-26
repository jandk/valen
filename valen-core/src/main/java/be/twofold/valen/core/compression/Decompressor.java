package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.nio.file.*;

public sealed interface Decompressor
    permits DeflateDecompressor, LZ4FrameDecompressor, LZDecompressor, NoneDecompressor, OodleDecompressor, OozDecompressor {

    static Decompressor deflate(boolean nowrap) {
        return new DeflateDecompressor(nowrap);
    }

    static Decompressor fastLZ() {
        return FastLZDecompressor.INSTANCE;
    }

    static Decompressor lz4Block() {
        return LZ4BlockDecompressor.INSTANCE;
    }

    static Decompressor lz4Frame() {
        return LZ4FrameDecompressor.INSTANCE;
    }

    static Decompressor none() {
        return NoneDecompressor.INSTANCE;
    }

    // TODO: Move this shit somewhere else...
    static Decompressor oodle() {
        return OodleDecompressor.download();
    }

    static Decompressor oodle(Path path) {
        return OodleDecompressor.load(path);
    }

    static Decompressor ooz(Path path) {
        return new OozDecompressor(path);
    }

    void decompress(Bytes src, Bytes.Mutable dst) throws IOException;

    default Bytes decompress(Bytes src, int size) throws IOException {
        var dst = Bytes.Mutable.allocate(size);
        decompress(src, dst);
        return dst;
    }

}
