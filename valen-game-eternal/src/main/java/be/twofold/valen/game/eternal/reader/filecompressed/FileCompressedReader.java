package be.twofold.valen.game.eternal.reader.filecompressed;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;

public final class FileCompressedReader implements AssetReader<Bytes, EternalAsset> {
    private final Decompressor decompressor;

    public FileCompressedReader(Decompressor decompressor) {
        this.decompressor = Check.notNull(decompressor, "decompressor");
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.CompFile
            /*&& !resource.id().extension().equals("entities")*/;
    }

    @Override
    public Bytes read(BinaryReader reader, EternalAsset resource) throws IOException {
        var header = FileCompressedHeader.read(reader);
        if (header.compressedSize() == -1) {
            return reader.readBytesStruct(header.uncompressedSize());
        }

        var compressed = reader.readBytesStruct(header.compressedSize());
        return decompressor.decompress(compressed, header.uncompressedSize());
    }
}
