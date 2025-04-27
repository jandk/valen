package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.greatcircle.reader.decl.*;
import be.twofold.valen.game.greatcircle.reader.decl.material2.*;
import be.twofold.valen.game.greatcircle.reader.decl.renderparm.*;
import be.twofold.valen.game.greatcircle.reader.image.*;
import be.twofold.valen.game.greatcircle.reader.md6mesh.*;
import be.twofold.valen.game.greatcircle.reader.staticmodel.*;
import be.twofold.valen.game.greatcircle.reader.streamdb.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.stream.*;

public final class GreatCircleArchive implements Archive<GreatCircleAssetID, GreatCircleAsset> {
    private final Container<Long, StreamDbEntry> streams;
    private final Container<GreatCircleAssetID, GreatCircleAsset> common;
    private final Container<GreatCircleAssetID, GreatCircleAsset> resources;
    private final AssetReaders<GreatCircleAsset> readers;

    GreatCircleArchive(
        Container<Long, StreamDbEntry> streams,
        Container<GreatCircleAssetID, GreatCircleAsset> common,
        Container<GreatCircleAssetID, GreatCircleAsset> resources
    ) {
        this.streams = Check.notNull(streams);
        this.common = Check.notNull(common);
        this.resources = Check.notNull(resources);

        var declReader = new DeclReader(this);
        this.readers = new AssetReaders<>(List.of(
            new ImageReader(this),
            new MaterialReader(this, declReader),
            new Md6MeshReader(this),
            new RenderParmReader(),
            new StaticModelReader(this)
        ));
    }


    @Override
    public <T> T loadAsset(GreatCircleAssetID identifier, Class<T> clazz) throws IOException {
        var asset = get(identifier).orElseThrow(FileNotFoundException::new);

        var buffer = resources.exists(asset.id())
            ? resources.read(asset.id(), null)
            : common.read(asset.id(), null);

        try (var source = DataSource.fromBuffer(buffer)) {
            return readers.read(asset, source, clazz);
        }
    }

    @Override
    public Optional<GreatCircleAsset> get(GreatCircleAssetID key) {
        return resources.get(key)
            .or(() -> common.get(key));
    }

    @Override
    public Stream<GreatCircleAsset> getAll() {
        return resources.getAll()
            .filter(asset -> asset.size() != 0)
            .distinct();
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
        resources.close();
    }
}
