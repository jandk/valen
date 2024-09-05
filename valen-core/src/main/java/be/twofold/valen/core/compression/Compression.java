package be.twofold.valen.core.compression;

import be.twofold.valen.core.compression.oodle.*;

import java.io.*;
import java.nio.*;

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

    public ByteBuffer decompress(ByteBuffer src, int dstLength) throws IOException {
        return decompressor.decompress(src, dstLength);
    }
}
