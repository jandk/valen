package be.twofold.valen.compression;

import be.twofold.valen.compression.oodle.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class DecompressorService {
    private final Map<CompressionType, Decompressor> decompressors = Map.of(
        CompressionType.None, new NullDecompressor(),
        CompressionType.Kraken, new OodleDecompressor(false),
        CompressionType.KrakenChunked, new OodleDecompressor(true)
    );

    @Inject
    DecompressorService() {
    }

    public ByteBuffer decompress(ByteBuffer src, int dstLength, CompressionType compressionType) throws IOException {
        return decompressors.get(compressionType).decompress(src, dstLength);
    }
}
