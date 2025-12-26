package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.darkages.reader.anim.*;
import be.twofold.valen.game.darkages.reader.basemodel.*;
import be.twofold.valen.game.darkages.reader.binaryfile.*;
import be.twofold.valen.game.darkages.reader.bink.*;
import be.twofold.valen.game.darkages.reader.decl.*;
import be.twofold.valen.game.darkages.reader.decl.material2.*;
import be.twofold.valen.game.darkages.reader.decl.renderparm.*;
import be.twofold.valen.game.darkages.reader.image.*;
import be.twofold.valen.game.darkages.reader.model.*;
import be.twofold.valen.game.darkages.reader.skeleton.*;
import be.twofold.valen.game.darkages.reader.strandshair.*;
import be.twofold.valen.game.darkages.reader.streamdb.*;
import be.twofold.valen.game.darkages.reader.vegetation.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class DarkAgesArchive extends Archive<DarkAgesAssetID, DarkAgesAsset> {
    private final Container<Long, StreamDbEntry> streams;
    private final Container<DarkAgesAssetID, DarkAgesAsset> common;
    private final Container<DarkAgesAssetID, DarkAgesAsset> resources;

    DarkAgesArchive(
        Container<Long, StreamDbEntry> streams,
        Container<DarkAgesAssetID, DarkAgesAsset> common,
        Container<DarkAgesAssetID, DarkAgesAsset> resources
    ) {
        this.streams = Check.nonNull(streams, "streams");
        this.common = Check.nonNull(common, "common");
        this.resources = Check.nonNull(resources, "resources");
    }

    @Override
    public List<AssetReader<?, DarkAgesAsset>> createReaders() {
        var declReader = new DeclReader(this);

        return List.of(
            declReader,
            new BinaryFileReader(),
            new BinkReader(this),
            new ImageReader(this),
            new MaterialReader(this, declReader),
            new Md6AnimReader(this),
            new Md6ModelReader(this, true),
            new Md6SkelReader(),
            new RenderParmReader(),
            new StaticModelReader(this),
            new StrandsHairReader(),
            new VegetationReader(this, true)
        );
    }

    @Override
    public Optional<DarkAgesAsset> get(DarkAgesAssetID key) {
        return resources.get(key)
            .or(() -> common.get(key));
    }

    @Override
    public Stream<DarkAgesAsset> getAll() {
        return resources.getAll()
            .filter(asset -> asset.size() != 0)
            .distinct();
    }

    @Override
    public Bytes read(DarkAgesAssetID identifier, Integer size) throws IOException {
        return resources.exists(identifier)
            ? resources.read(identifier, null)
            : common.read(identifier, null);
    }

    public boolean containsStream(long identifier) {
        return streams.exists(identifier);
    }

    public Bytes readStream(long identifier, int size) throws IOException {
        return streams.read(identifier, size < 0 ? null : size);
    }

    @Override
    public void close() throws IOException {
        streams.close();
        common.close();
        resources.close();
    }
}
