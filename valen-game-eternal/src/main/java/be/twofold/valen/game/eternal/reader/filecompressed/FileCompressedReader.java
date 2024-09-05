package be.twofold.valen.game.eternal.reader.filecompressed;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.reader.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.nio.*;

public final class FileCompressedReader implements ResourceReader<byte[]> {
    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.CompFile
            && !key.name().extension().equals("entities");
    }

    @Override
    public byte[] read(DataSource source, Asset asset) throws IOException {
        var header = FileCompressedHeader.read(source);
        var compressed = source.readBytes(header.compressedSize());

        return Buffers.toArray(Compression.Oodle
            .decompress(ByteBuffer.wrap(compressed), header.uncompressedSize()));
    }
}
