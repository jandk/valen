package be.twofold.valen.core.compression;

import java.io.*;
import java.nio.*;

public abstract class Decompressor {
    public abstract ByteBuffer decompress(ByteBuffer src, int dstLength) throws IOException;
}
