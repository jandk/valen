package be.twofold.valen.reader.compfile;

import be.twofold.valen.core.util.*;
import be.twofold.valen.oodle.*;
import be.twofold.valen.reader.*;

public final class CompFileReader implements ResourceReader<byte[]> {
    @Override
    public byte[] read(BetterBuffer buffer) {
        CompFileHeader header = CompFileHeader.read(buffer);

        byte[] compressed = buffer.getBytes(header.compressedSize());
        return OodleDecompressor.decompress(compressed, header.uncompressedSize());
    }
}
