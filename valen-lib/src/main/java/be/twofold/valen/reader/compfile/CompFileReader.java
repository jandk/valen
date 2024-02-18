package be.twofold.valen.reader.compfile;

import be.twofold.valen.core.util.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;

public final class CompFileReader implements ResourceReader<byte[]> {
    @Override
    public byte[] read(BetterBuffer buffer, Resource resource, FileManager manager) {
        CompFileHeader header = CompFileHeader.read(buffer);

        byte[] compressed = buffer.getBytes(header.compressedSize());
        return OodleDecompressor.decompress(compressed, header.uncompressedSize());
    }
}
