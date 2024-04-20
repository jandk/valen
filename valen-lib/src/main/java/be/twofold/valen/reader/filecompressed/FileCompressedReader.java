package be.twofold.valen.reader.filecompressed;

import be.twofold.valen.core.util.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

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
    public byte[] read(BetterBuffer buffer, Resource resource) {
        FileCompressedHeader header = FileCompressedHeader.read(buffer);

        byte[] compressed = buffer.getBytes(header.compressedSize());
        return OodleDecompressor.decompress(compressed, header.uncompressedSize());
    }

}
