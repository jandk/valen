package be.twofold.valen.reader.filecompressed;

import be.twofold.valen.core.io.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.io.*;

public final class FileCompressedReader implements ResourceReader<byte[]> {

    @Inject
    public FileCompressedReader() {
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
        return OodleDecompressor.decompress(compressed, header.uncompressedSize());
    }

}
