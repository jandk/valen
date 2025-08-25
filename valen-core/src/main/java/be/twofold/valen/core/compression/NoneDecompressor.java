package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.collect.*;

import java.io.*;

final class NoneDecompressor implements Decompressor {

    @Override
    public void decompress(Bytes src, MutableBytes dst) throws IOException {
        if (src == dst) {
            return;
        }

        if (src.size() != dst.size()) {
            throw new IOException("src.size() (" + src.size() + ") and dst.size() (" + dst.size() + ") do not match");
        }

        src.copyTo(dst, 0);
    }
}
