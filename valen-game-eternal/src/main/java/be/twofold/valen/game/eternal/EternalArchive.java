package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.reader.*;
import be.twofold.valen.game.eternal.reader.decl.*;
import be.twofold.valen.game.eternal.reader.decl.material2.*;
import be.twofold.valen.game.eternal.reader.decl.renderparm.*;
import be.twofold.valen.game.eternal.reader.image.*;
import be.twofold.valen.game.eternal.reader.mapfilestaticinstances.*;
import be.twofold.valen.game.eternal.reader.md6model.*;
import be.twofold.valen.game.eternal.reader.md6skel.*;
import be.twofold.valen.game.eternal.reader.staticmodel.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.util.*;

public final class EternalArchive implements Archive {
    private final StreamDbCollection streams;
    private final ResourcesCollection resources;
    private final List<ResourceReader<?>> readers;

    public EternalArchive(StreamDbCollection streams, ResourcesCollection resources) {
        this.streams = Check.notNull(streams);
        this.resources = Check.notNull(resources);

        var declReader = new DeclReader(this);
        this.readers = List.of(
            declReader,
            new ImageReader(this),
            new MapFileStaticInstancesReader(this),
            new MaterialReader(this, declReader),
            new Md6ModelReader(this),
            new Md6SkelReader(),
            new RenderParmReader(),
            new StaticModelReader(this)
        );
    }

    @Override
    public List<Asset> assets() {
        return resources.getEntries().stream()
            .map(this::toAsset)
            .distinct()
            .sorted()
            .toList();
    }

    private Asset toAsset(Resource resource) {
        return new Asset(
            resource.key(),
            mapType(resource.type()),
            resource.uncompressedSize(),
            Map.of("hash", resource.hash())
        );
    }

    @Override
    public boolean exists(AssetID id) {
        return resources.get((ResourceKey) id).isPresent();
    }

    @Override
    public Object loadAsset(AssetID id) throws IOException {
        var resource = resources.get((ResourceKey) id)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + id));

        var reader = readers.stream()
            .filter(r -> r.canRead(resource.key()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No reader found for resource: " + resource));

        var bytes = resources.read(resource);
        try (var source = DataSource.fromArray(bytes)) {
            return reader.read(source, toAsset(resource));
        }
    }

    @Override
    public byte[] loadRawAsset(AssetID id) throws IOException {
        var resource = resources.get((ResourceKey) id)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + id));

        return resources.read(resource);
    }

    public boolean containsStream(long identifier) {
        return streams.exists(identifier);
    }

    public byte[] readStream(long identifier, int uncompressedSize) throws IOException {
        return streams.read(identifier, uncompressedSize);
    }

    private AssetType mapType(ResourceType type) {
        return switch (type) {
            case Image -> AssetType.Texture;
            case BaseModel, Model -> AssetType.Model;
            default -> AssetType.Binary;
        };
    }
}
