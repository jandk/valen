package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.reader.decl.*;
import be.twofold.valen.game.eternal.reader.decl.material2.*;
import be.twofold.valen.game.eternal.reader.decl.renderparm.*;
import be.twofold.valen.game.eternal.reader.image.*;
import be.twofold.valen.game.eternal.reader.mapfilestaticinstances.*;
import be.twofold.valen.game.eternal.reader.md6model.*;
import be.twofold.valen.game.eternal.reader.md6skel.*;
import be.twofold.valen.game.eternal.reader.staticmodel.*;
import be.twofold.valen.game.eternal.reader.streamdb.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class EternalArchive implements Archive {
    private final Container<Long, StreamDbEntry> streams;
    private final Container<EternalAssetID, EternalAsset> common;
    private final Container<EternalAssetID, EternalAsset> resources;
    private final AssetReaders<EternalAsset> readers;

    EternalArchive(
        Container<Long, StreamDbEntry> streams,
        Container<EternalAssetID, EternalAsset> common,
        Container<EternalAssetID, EternalAsset> resources
    ) {
        this.streams = Check.notNull(streams, "streams");
        this.common = Check.notNull(common, "common");
        this.resources = Check.notNull(resources, "resources");

        var declReader = new DeclReader(this);

        this.readers = new AssetReaders<>(List.of(
            declReader,
            new ImageReader(this),
            new MapFileStaticInstancesReader(this),
            new MaterialReader(this, declReader),
            new Md6ModelReader(this),
            new Md6SkelReader(),
            new RenderParmReader(),
            new StaticModelReader(this)
        ));
    }

    @Override
    public Stream<? extends Asset> assets() {
        return resources.getAll()
            .filter(asset -> asset.size() != 0)
            .distinct();
    }

    @Override
    public Optional<EternalAsset> getAsset(AssetID identifier) {
        return resources.get((EternalAssetID) identifier)
            .or(() -> common.get((EternalAssetID) identifier));
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        var resource = getAsset(identifier)
            .orElseThrow(FileNotFoundException::new);

        var bytes = resources.get(resource.key()).isPresent()
            ? resources.read(resource.key())
            : common.read(resource.key());

        if (clazz == byte[].class) {
            return (T) bytes;
        }

        try (var source = DataSource.fromArray(bytes)) {
            return readers.read(resource, source, clazz);
        }
    }

    public boolean containsStream(long identifier) {
        return streams.get(identifier).isPresent();
    }

    public byte[] readStream(long identifier, int uncompressedSize) throws IOException {
        return streams.read(identifier, uncompressedSize);
    }
}
