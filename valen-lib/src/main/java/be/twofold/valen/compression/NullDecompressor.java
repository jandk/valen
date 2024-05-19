package be.twofold.valen.compression;

import java.io.*;
import java.nio.*;

public final class NullDecompressor implements Decompressor {
    @Override
    public void decompress(ByteBuffer src, ByteBuffer dst) throws IOException {
        dst.put(src);
        dst.flip();
    }
}
