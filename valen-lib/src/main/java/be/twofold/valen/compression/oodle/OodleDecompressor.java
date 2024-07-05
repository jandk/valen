package be.twofold.valen.compression.oodle;

import be.twofold.valen.compression.*;

import java.io.*;
import java.nio.*;

public final class OodleDecompressor implements Decompressor {
    @Override
    public void decompress(ByteBuffer src, ByteBuffer dst) throws IOException {
        Oodle.decompress(src, dst);
    }
}
