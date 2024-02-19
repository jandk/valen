package be.twofold.valen.reader.compfile;

import be.twofold.valen.core.util.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

public final class CompFileReader implements ResourceReader<byte[]> {

    @Inject
    public CompFileReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.CompFile;
    }

    @Override
    public byte[] read(BetterBuffer buffer, Resource resource) {
        CompFileHeader header = CompFileHeader.read(buffer);

        byte[] compressed = buffer.getBytes(header.compressedSize());
        return OodleDecompressor.decompress(compressed, header.uncompressedSize());
    }

}
