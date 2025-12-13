package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.collect.*;

import java.io.*;

final class NoneDecompressor implements Decompressor {
    static final NoneDecompressor INSTANCE = new NoneDecompressor();

    private NoneDecompressor() {
    }

    @Override
    public void decompress(Bytes src, MutableBytes dst) throws IOException {
        if (src == dst) {
            return;
        }

        if (src.length() != dst.length()) {
            throw new IOException("src.size() (" + src.length() + ") and dst.size() (" + dst.length() + ") do not match");
        }

        src.copyTo(dst, 0);
    }
}
