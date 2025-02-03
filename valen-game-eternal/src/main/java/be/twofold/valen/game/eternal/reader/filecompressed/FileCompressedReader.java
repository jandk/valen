package be.twofold.valen.game.eternal.reader.filecompressed;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.nio.*;

public final class FileCompressedReader implements AssetReader<ByteBuffer, Resource> {
    private final Decompressor decompressor;

    public FileCompressedReader(Decompressor decompressor) {
        this.decompressor = Check.notNull(decompressor, "decompressor");
    }

    @Override
    public boolean canRead(Resource resource) {
        return resource.key().type() == ResourceType.CompFile
            && !resource.key().name().extension().equals("entities");
    }

    @Override
    public ByteBuffer read(DataSource source, Resource resource) throws IOException {
        var header = FileCompressedHeader.read(source);
        if (header.compressedSize() == -1) {
            return ByteBuffer.wrap(source.readBytes(header.uncompressedSize()));
        }

        var compressed = source.readBytes(header.compressedSize());
        return ByteBuffer.wrap(decompressor.decompress(compressed, header.uncompressedSize()));
    }
}
