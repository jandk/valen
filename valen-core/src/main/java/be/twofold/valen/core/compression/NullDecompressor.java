package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.*;

import java.io.*;

final class NullDecompressor extends Decompressor {

    @Override
    public void decompress(
        byte[] source, int sourceOffset, int sourceLength,
        byte[] target, int targetOffset, int targetLength
    ) throws IOException {
        Check.fromIndexSize(sourceOffset, sourceLength, source.length);
        Check.fromIndexSize(targetOffset, targetLength, target.length);

        if (sourceLength != targetLength) {
            throw new IOException("Invalid decompressed size");
        }
    }
}
