package be.twofold.valen.core.compression;

import java.io.*;

public abstract class Decompressor {
    public abstract void decompress(
        byte[] source, int sourceOffset, int sourceLength,
        byte[] target, int targetOffset, int targetLength
    ) throws IOException;
}
