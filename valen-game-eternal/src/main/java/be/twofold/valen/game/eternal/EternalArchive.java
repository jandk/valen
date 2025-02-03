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
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.util.*;

public final class EternalArchive implements Archive {
    private final Container<Long, StreamDbEntry> streams;
    private final Container<ResourceKey, Resource> common;
    private final Container<ResourceKey, Resource> resources;
    private final AssetReaders<Resource> readers;

    EternalArchive(
        Container<Long, StreamDbEntry> streams,
        Container<ResourceKey, Resource> common,
        Container<ResourceKey, Resource> resources
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
    public List<Resource> assets() {
        return resources.getAll()
            .filter(asset -> asset.size() != 0)
            .distinct().sorted()
            .toList();
    }

    @Override
    public Optional<Resource> get(AssetID identifier) {
        return resources.get((ResourceKey) identifier)
            .or(() -> common.get((ResourceKey) identifier));
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        var resource = get(identifier)
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
