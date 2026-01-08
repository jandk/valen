package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.greatcircle.reader.decl.*;
import be.twofold.valen.game.greatcircle.reader.decl.material2.*;
import be.twofold.valen.game.greatcircle.reader.decl.renderparm.*;
import be.twofold.valen.game.greatcircle.reader.deformmodel.*;
import be.twofold.valen.game.greatcircle.reader.hair.*;
import be.twofold.valen.game.greatcircle.reader.image.*;
import be.twofold.valen.game.greatcircle.reader.md6mesh.*;
import be.twofold.valen.game.greatcircle.reader.md6skl.*;
import be.twofold.valen.game.greatcircle.reader.staticmodel.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class GreatCircleArchive extends Archive<GreatCircleAssetID, GreatCircleAsset> {
    private final BinaryStore<Long> streams;
    private final Container<GreatCircleAssetID, GreatCircleAsset> common;
    private final Container<GreatCircleAssetID, GreatCircleAsset> resources;

    GreatCircleArchive(
        BinaryStore<Long> streams,
        Container<GreatCircleAssetID, GreatCircleAsset> common,
        Container<GreatCircleAssetID, GreatCircleAsset> resources
    ) {
        this.streams = Check.nonNull(streams, "streams");
        this.common = Check.nonNull(common, "common");
        this.resources = Check.nonNull(resources, "resources");
    }

    BinaryStore<Long> streams() {
        return streams;
    }

    @Override
    public List<AssetReader<?, GreatCircleAsset>> createReaders() {
        var declReader = new DeclReader(this);
        return List.of(
            new DeformModelReader(this, streams, true),
            new HairReader(),
            new ImageReader(streams),
            new MaterialReader(this, declReader),
            new Md6MeshReader(this, streams, true),
            new Md6SklReader(),
            new RenderParmReader(),
            new StaticModelReader(this, streams, true)
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
            ? resources.read(identifier)
            : common.read(identifier);
    }

    @Override
    public void close() throws IOException {
        streams.close();
        resources.close();
    }
}
