package be.twofold.valen.game.eternal.reader.filecompressed;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class FileCompressedReader implements AssetReader.Binary<Bytes, EternalAsset> {
    public FileCompressedReader() {
    }

    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.of("compfile")
            /*&& !resource.id().extension().equals("entities")*/;
    }

    @Override
    public Bytes read(BinaryReader reader, EternalAsset asset, LoadingContext context) throws IOException {
        var header = FileCompressedHeader.read(reader);
        if (header.compressedSize() == -1) {
            return reader.readBytes(header.uncompressedSize());
        }

        var compressed = reader.readBytes(header.compressedSize());
        return context.decompress(CompressionType.OODLE, compressed, header.uncompressedSize());
    }
}
