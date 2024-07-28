package be.twofold.valen.compression;

import java.io.*;
import java.nio.*;

public interface Decompressor {
    ByteBuffer decompress(ByteBuffer src, int dstLength) throws IOException;
}
