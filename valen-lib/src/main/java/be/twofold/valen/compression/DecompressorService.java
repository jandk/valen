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
        CompressionType.Kraken, new OodleDecompressor(),
        CompressionType.KrakenChunked, new OodleChunkedDecompressor()
    );

    @Inject
    DecompressorService() {
    }

    public void decompress(ByteBuffer src, ByteBuffer dst, CompressionType compressionType) throws IOException {
        decompressors.get(compressionType).decompress(src, dst);
    }
}
