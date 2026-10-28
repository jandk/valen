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
    public Bytes read(BinarySource source, EternalAsset asset, LoadingContext context) throws IOException {
        var header = FileCompressedHeader.read(source);
        if (header.compressedSize() == -1) {
            return source.readBytes(header.uncompressedSize());
        }

        var compressed = source.readBytes(header.compressedSize());
        return context.decompress(CompressionType.OODLE, compressed, header.uncompressedSize());
    }
}
