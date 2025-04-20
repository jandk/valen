package be.twofold.valen.core.compression;

import java.io.*;
import java.nio.*;

final class NoneDecompressor implements Decompressor {

    @Override
    public void decompress(ByteBuffer src, ByteBuffer dst) throws IOException {
        if (src == dst) {
            return;
        }

        if (src.remaining() != dst.remaining()) {
            throw new IOException("src.remaining() (" + src.remaining() + ") and dst.remaining() (" + dst.remaining() + ") do not match");
        }

        dst.put(src);
    }
}
