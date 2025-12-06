package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.greatcircle.reader.decl.*;
import be.twofold.valen.game.greatcircle.reader.decl.material2.*;
import be.twofold.valen.game.greatcircle.reader.decl.renderparm.*;
import be.twofold.valen.game.greatcircle.reader.deformmodel.*;
import be.twofold.valen.game.greatcircle.reader.hair.*;
import be.twofold.valen.game.greatcircle.reader.image.*;
import be.twofold.valen.game.greatcircle.reader.md6mesh.*;
import be.twofold.valen.game.greatcircle.reader.md6skl.*;
import be.twofold.valen.game.greatcircle.reader.staticmodel.*;
import be.twofold.valen.game.greatcircle.reader.streamdb.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class GreatCircleArchive extends Archive<GreatCircleAssetID, GreatCircleAsset> {
    private final Container<Long, StreamDbEntry> streams;
    private final Container<GreatCircleAssetID, GreatCircleAsset> common;
    private final Container<GreatCircleAssetID, GreatCircleAsset> resources;

    GreatCircleArchive(
        Container<Long, StreamDbEntry> streams,
        Container<GreatCircleAssetID, GreatCircleAsset> common,
        Container<GreatCircleAssetID, GreatCircleAsset> resources
    ) {
        this.streams = Check.notNull(streams, "streams");
        this.common = Check.notNull(common, "common");
        this.resources = Check.notNull(resources, "resources");
    }

    @Override
    public List<AssetReader<?, GreatCircleAsset>> createReaders() {
        var declReader = new DeclReader(this);
        return List.of(
            new DeformModelReader(this, true),
            new HairReader(),
            new ImageReader(this),
            new MaterialReader(this, declReader),
            new Md6MeshReader(this),
            new Md6SklReader(),
            new RenderParmReader(),
            new StaticModelReader(this)
        );
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

    @Override
    public Bytes read(GreatCircleAssetID identifier, Integer size) throws IOException {
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
        resources.close();
    }
}
