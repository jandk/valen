package be.twofold.valen.game.darkages;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.darkages.reader.decl.*;
import be.twofold.valen.game.darkages.reader.decl.material2.*;
import be.twofold.valen.game.darkages.reader.decl.renderparm.*;
import be.twofold.valen.game.darkages.reader.image.*;
import be.twofold.valen.game.darkages.reader.model.*;
import be.twofold.valen.game.darkages.reader.skeleton.*;
import be.twofold.valen.game.darkages.reader.streamdb.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.stream.*;

public final class DarkAgesArchive implements Archive<DarkAgesAssetID, DarkAgesAsset> {
    private final Container<Long, StreamDbEntry> streams;
    private final Container<DarkAgesAssetID, DarkAgesAsset> common;
    private final Container<DarkAgesAssetID, DarkAgesAsset> resources;
    private final AssetReaders<DarkAgesAsset> readers;

    DarkAgesArchive(
        Container<Long, StreamDbEntry> streams,
        Container<DarkAgesAssetID, DarkAgesAsset> common,
        Container<DarkAgesAssetID, DarkAgesAsset> resources,
        Decompressor decompressor
    ) {
        this.streams = Check.notNull(streams, "streams");
        this.common = Check.notNull(common, "common");
        this.resources = Check.notNull(resources, "resources");
        Check.notNull(decompressor, "decompressor");

        var declReader = new DeclReader(this);
        this.readers = new AssetReaders<>(List.of(
            declReader,

            new ImageReader(this),
            new MaterialReader(this, declReader),
            new Md6SkelReader(),
            new RenderParmReader(),
            new StaticModelReader(this)
        ));
    }

    @Override
    public Stream<DarkAgesAsset> getAll() {
        return resources.getAll()
            .filter(asset -> asset.size() != 0)
            .distinct();
    }

    @Override
    public Optional<DarkAgesAsset> get(DarkAgesAssetID key) {
        return resources.get(key)
            .or(() -> common.get(key));
    }

    @Override
    public <T> T loadAsset(DarkAgesAssetID identifier, Class<T> clazz) throws IOException {
        var asset = get(identifier).orElseThrow(FileNotFoundException::new);

        var buffer = resources.exists(asset.id())
            ? resources.read(asset.id(), null)
            : common.read(asset.id(), null);

        try (var source = DataSource.fromBuffer(buffer)) {
            return readers.read(asset, source, clazz);
        }
    }

    public boolean containsStream(long identifier) {
        return streams.exists(identifier);
    }

    public ByteBuffer readStream(long identifier, int size) throws IOException {
        return streams.read(identifier, size);
    }

    @Override
    public void close() throws IOException {
        streams.close();
        common.close();
        resources.close();
    }
}
