package be.twofold.valen.reader.filecompressed;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.io.*;
import java.nio.*;

public final class FileCompressedReader implements ResourceReader<byte[]> {
    @Inject
    FileCompressedReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.CompFile
            && !entry.name().extension().equals("entities");
    }

    @Override
    public byte[] read(DataSource source, Resource resource) throws IOException {
        var header = FileCompressedHeader.read(source);
        var compressed = source.readBytes(header.compressedSize());

        return Decompressor
            .forType(CompressionType.Kraken)
            .decompress(ByteBuffer.wrap(compressed), header.uncompressedSize())
            .array();
    }
}
