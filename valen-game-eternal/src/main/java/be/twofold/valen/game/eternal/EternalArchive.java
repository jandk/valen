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
    private final ResourcesCollection common;
    private final ResourcesCollection resources;
    private final List<ResourceReader<?>> readers;

    EternalArchive(StreamDbCollection streams, ResourcesCollection common, ResourcesCollection resources) {
        this.streams = Check.notNull(streams, "streams");
        this.common = Check.notNull(common, "common");
        this.resources = Check.notNull(resources, "resources");

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
            .filter(asset -> asset.size() != 0)
            .distinct()
            .sorted()
            .toList();
    }

    private Asset toAsset(Resource resource) {
        var properties = new HashMap<String, Object>();
        properties.put("hash", resource.hash());
        properties.put("Type", resource.key().type().toString());
        if (resource.key().variation() != ResourceVariation.None) {
            properties.put("Variation", resource.key().variation());
        }

        return new Asset(
            resource.key(),
            mapType(resource.key().type()),
            resource.uncompressedSize(),
            Map.copyOf(properties)
        );
    }

    private AssetType<?> mapType(ResourceType type) {
        return switch (type) {
            case Image -> AssetType.TEXTURE;
            case BaseModel, Model -> AssetType.MODEL;
            default -> AssetType.BINARY;
        };
    }

    @Override
    public boolean exists(AssetID identifier) {
        return resources.get((ResourceKey) identifier)
            .or(() -> common.get((ResourceKey) identifier))
            .isPresent();
    }

    @Override
    public Asset getAsset(AssetID identifier) {
        var resource = resources.get((ResourceKey) identifier)
            .or(() -> common.get((ResourceKey) identifier))
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + identifier));

        return toAsset(resource);
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        var resource = resources.get((ResourceKey) identifier)
            .or(() -> common.get((ResourceKey) identifier))
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + identifier));

        byte[] bytes;
        if (resources.get(resource.key()).isPresent()) {
            bytes = resources.read(resource);
        } else {
            bytes = common.read(resource);
        }

        if (clazz == byte[].class) {
            return (T) bytes;
        }

        var reader = readers.stream()
            .filter(r -> r.canRead(resource.key()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No reader found for resource: " + resource));

        try (var source = DataSource.fromArray(bytes)) {
            return clazz.cast(reader.read(source, toAsset(resource)));
        }
    }

    public boolean containsStream(long identifier) {
        return streams.exists(identifier);
    }

    public byte[] readStream(long identifier, int uncompressedSize) throws IOException {
        return streams.read(identifier, uncompressedSize);
    }
}
