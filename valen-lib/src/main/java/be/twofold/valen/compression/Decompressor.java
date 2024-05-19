package be.twofold.valen.compression;

import java.io.*;
import java.nio.*;

public interface Decompressor {
    void decompress(ByteBuffer src, ByteBuffer dst) throws IOException;
}
