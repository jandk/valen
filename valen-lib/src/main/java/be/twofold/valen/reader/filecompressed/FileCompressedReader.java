package be.twofold.valen.reader.filecompressed;

import be.twofold.valen.compression.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.io.*;
import java.nio.*;

public final class FileCompressedReader implements ResourceReader<byte[]> {
    private final DecompressorService decompressorService;

    @Inject
    public FileCompressedReader(DecompressorService decompressorService) {
        this.decompressorService = decompressorService;
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.CompFile
            && !entry.name().extension().equals("entities");
    }

    @Override
    public byte[] read(DataSource source, Resource resource) throws IOException {
        FileCompressedHeader header = FileCompressedHeader.read(source);

        byte[] compressed = source.readBytes(header.compressedSize());
        byte[] decompressed = new byte[header.uncompressedSize()];
        decompressorService.decompress(ByteBuffer.wrap(compressed), ByteBuffer.wrap(decompressed), CompressionType.Kraken);
        return decompressed;
    }
}
