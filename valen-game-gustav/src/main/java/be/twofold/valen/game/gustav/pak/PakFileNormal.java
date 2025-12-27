package be.twofold.valen.game.gustav.pak;

import be.twofold.valen.core.util.*;
import be.twofold.valen.game.gustav.*;
import be.twofold.valen.game.gustav.reader.pak.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

final class PakFileNormal extends PakFile {
    private final List<BinarySource> readers;

    PakFileNormal(Path path) throws IOException {
        var reader = BinarySource.open(path);
        var pak = Pak.read(reader);

        if (pak.header().flags().contains(PakFlag.Solid)) {
            throw new IOException("Not a normal pak file: " + path);
        }

        var readers = new ArrayList<BinarySource>();
        readers.add(reader);
        if (pak.header().numParts() > 1) {
            var basename = Filenames.getBaseName(path.getFileName().toString());
            for (var i = 1; i < pak.header().numParts(); i++) {
                readers.add(BinarySource.open(path.getParent().resolve(basename + "_" + i + ".pak")));
            }
        }
        this.readers = List.copyOf(readers);
        super(pak.entries());
    }

    @Override
    public Bytes read(GustavAssetID key, Integer size) throws IOException {
        var asset = index.get(key);
        Check.state(asset != null, () -> "Resource not found: " + key.fullName());
        if (!(asset instanceof GustavAsset.Pak pakAsset)) {
            throw new IOException("Not a pak asset: " + key.fullName());
        }

        var reader = readers.get(pakAsset.entry().archivePart());
        reader.position(pakAsset.entry().offset());
        var compressed = reader.readBytes(pakAsset.entry().compressedSize());

        var decompressor = getDecompressor(pakAsset.entry().flags());
        if (decompressor == Decompressor.none()) {
            return compressed;
        }
        return decompressor
            .decompress(compressed, pakAsset.entry().size());
    }

    private Decompressor getDecompressor(Set<Compression> flags) {
        if (flags.contains(Compression.METHOD_ZLIB)) {
            return Decompressor.deflate(false);
        } else if (flags.contains(Compression.METHOD_LZ4)) {
            return Decompressor.lz4Block();
        } else if (flags.contains(Compression.METHOD_ZSTD)) {
            throw new UnsupportedOperationException("ZSTD is currently not supported");
        } else {
            return Decompressor.none();
        }
    }

    @Override
    public void close() throws IOException {
        for (var reader : readers) {
            reader.close();
        }
    }
}
