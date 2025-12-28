package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.colossus.reader.image.*;
import be.twofold.valen.game.colossus.reader.texdb.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class ColossusArchive extends Archive<ColossusAssetID, ColossusAsset> {
    private final Decompressor decompressor;
    private final Container<ColossusAssetID, ColossusAsset> resources;
    private final Container<Long, TexDbEntry> texDBs;

    public ColossusArchive(
        Container<ColossusAssetID, ColossusAsset> resources,
        Container<Long, TexDbEntry> texDBs,
        Decompressor decompressor
    ) {
        this.resources = Check.nonNull(resources, "resources");
        this.texDBs = Check.nonNull(texDBs, "texDBs");
        this.decompressor = Check.nonNull(decompressor, "decompressor");
    }

    @Override
    public List<AssetReader<?, ColossusAsset>> createReaders() {
        return List.of(
            new ImageReader(this, decompressor, true)
        );
    }

    @Override
    public Optional<ColossusAsset> get(ColossusAssetID key) {
        return resources.get(key);
    }

    @Override
    public Stream<ColossusAsset> getAll() {
        return resources.getAll();
    }

    @Override
    public Bytes read(ColossusAssetID key, Integer size) throws IOException {
        return resources.read(key, null);
    }


    public boolean containsStream(long hash) {
        return texDBs.exists(hash);
    }

    public Bytes readStream(long hash, int compressedSize, int uncompressedSize) throws IOException {
        var compressed = readStreamRaw(hash, compressedSize);
        if (compressed.length() == uncompressedSize) {
            return compressed;
        }

        return decompressor.decompress(compressed, uncompressedSize);
    }

    public Bytes readStreamRaw(long hash, int size) throws IOException {
        return texDBs.read(hash, size);
    }


    @Override
    public void close() throws IOException {
        resources.close();
        texDBs.close();
    }
}
