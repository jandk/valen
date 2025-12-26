package be.twofold.valen.game.eternal;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.eternal.reader.binaryfile.*;
import be.twofold.valen.game.eternal.reader.decl.*;
import be.twofold.valen.game.eternal.reader.decl.material2.*;
import be.twofold.valen.game.eternal.reader.decl.renderparm.*;
import be.twofold.valen.game.eternal.reader.file.FileReader;
import be.twofold.valen.game.eternal.reader.filecompressed.*;
import be.twofold.valen.game.eternal.reader.image.*;
import be.twofold.valen.game.eternal.reader.json.*;
import be.twofold.valen.game.eternal.reader.mapfilestaticinstances.*;
import be.twofold.valen.game.eternal.reader.md6anim.*;
import be.twofold.valen.game.eternal.reader.md6model.*;
import be.twofold.valen.game.eternal.reader.md6skel.*;
import be.twofold.valen.game.eternal.reader.staticmodel.*;
import be.twofold.valen.game.eternal.reader.streamdb.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class EternalArchive extends Archive<EternalAssetID, EternalAsset> {
    private final Container<Long, StreamDbEntry> streams;
    private final Container<EternalAssetID, EternalAsset> common;
    private final Container<EternalAssetID, EternalAsset> resources;
    private final Decompressor decompressor;

    EternalArchive(
        Container<Long, StreamDbEntry> streams,
        Container<EternalAssetID, EternalAsset> common,
        Container<EternalAssetID, EternalAsset> resources,
        Decompressor decompressor
    ) {
        this.streams = Check.nonNull(streams, "streams");
        this.common = Check.nonNull(common, "common");
        this.resources = Check.nonNull(resources, "resources");
        this.decompressor = Check.nonNull(decompressor, "decompressor");
    }

    @Override
    public List<AssetReader<?, EternalAsset>> createReaders() {
        var declReader = new DeclReader(this);

        return List.of(
            declReader,
            // Binary converters
            new BinaryFileReader(),
            new FileCompressedReader(decompressor),
            new FileReader(),
            new JsonReader(),

            // Actual readers
            new ImageReader(this),
            new MapFileStaticInstancesReader(this),
            new MaterialReader(this, declReader),
            new Md6AnimReader(this),
            new Md6ModelReader(this),
            new Md6SkelReader(),
            new RenderParmReader(),
            new StaticModelReader(this)
        );
    }

    @Override
    public Optional<EternalAsset> get(EternalAssetID key) {
        return resources.get(key)
            .or(() -> common.get(key));
    }

    @Override
    public Stream<EternalAsset> getAll() {
        return resources.getAll()
            .filter(asset -> asset.size() != 0)
            .distinct();
    }

    @Override
    public Bytes read(EternalAssetID identifier, Integer size) throws IOException {
        return resources.exists(identifier)
            ? resources.read(identifier, null)
            : common.read(identifier, null);
    }

    public boolean containsStream(long identifier) {
        return streams.exists(identifier);
    }

    public Bytes readStream(long identifier, int size) throws IOException {
        return streams.read(identifier, size);
    }

    @Override
    public void close() throws IOException {
        streams.close();
        common.close();
        resources.close();
    }
}
