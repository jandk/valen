package be.twofold.valen.core.compression;

import be.twofold.valen.core.compression.oodle.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public abstract class Decompressor {
    private static final Map<CompressionType, Decompressor> decompressors = Map.of(
        CompressionType.None, new NullDecompressor(),
        CompressionType.Kraken, new OodleDecompressor(false),
        CompressionType.KrakenChunked, new OodleDecompressor(true)
    );

    public static Decompressor forType(CompressionType compressionType) {
        return decompressors.get(compressionType);
    }

    public abstract ByteBuffer decompress(ByteBuffer src, int dstLength) throws IOException;
}
