package be.twofold.valen.core.compression;

import java.io.*;

public enum Compression {
    None(new NullDecompressor()),
    InflateRaw(new InflateDecompressor(true)),
    InflateZlib(new InflateDecompressor(false)),
    LZ4Block(new LZ4Decompressor()),
    Oodle(new OodleDecompressor(false)),
    OodleChunked(new OodleDecompressor(true)),
    ;

    private final Decompressor decompressor;

    Compression(Decompressor decompressor) {
        this.decompressor = decompressor;
    }

    public byte[] decompress(byte[] source, int dstLength) throws IOException {
        byte[] target = new byte[dstLength];
        decompressor.decompress(
            source, 0, source.length,
            target, 0, target.length
        );
        return target;
    }
}
