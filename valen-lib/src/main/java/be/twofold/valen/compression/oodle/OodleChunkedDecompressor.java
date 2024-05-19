package be.twofold.valen.compression.oodle;

import be.twofold.valen.compression.*;

import java.io.*;
import java.nio.*;

public final class OodleChunkedDecompressor implements Decompressor {
    @Override
    public void decompress(ByteBuffer src, ByteBuffer dst) throws IOException {
        Oodle.decompress(src.slice(12, src.remaining() - 12), dst);
        dst.flip();
    }
}
