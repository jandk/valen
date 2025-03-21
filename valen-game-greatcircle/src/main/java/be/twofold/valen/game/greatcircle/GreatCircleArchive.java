package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.greatcircle.reader.*;
import be.twofold.valen.game.greatcircle.reader.decl.*;
import be.twofold.valen.game.greatcircle.reader.decl.material2.*;
import be.twofold.valen.game.greatcircle.reader.decl.renderparm.*;
import be.twofold.valen.game.greatcircle.reader.image.*;
import be.twofold.valen.game.greatcircle.reader.staticmodel.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.io.*;
import java.util.*;

public final class GreatCircleArchive implements Archive {
    private final StreamDbCollection streams;
    private final ResourcesCollection resources;
    private final List<ResourceReader<?>> readers;

    GreatCircleArchive(StreamDbCollection streams, ResourcesCollection resources) {
        this.streams = Check.notNull(streams);
        this.resources = Check.notNull(resources);

        var declReader = new DeclReader(this);
        this.readers = List.of(
            new ImageReader(this),
            new StaticModelReader(this),
            new MaterialReader(this, declReader),
            new RenderParmReader()
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
        return new Asset(
            resource.key(),
            mapType(resource.key().type()),
            resource.uncompressedSize(),
            Map.of(
                "hash", resource.hash(),
                "version", resource.version()
            )
        );
    }

    private AssetType<?> mapType(ResourceType type) {
        return switch (type) {
            case image -> AssetType.TEXTURE;
            case model -> AssetType.MODEL;
            default -> AssetType.BINARY;
        };
    }

    @Override
    public boolean exists(AssetID id) {
        return resources.get((ResourceKey) id)
            .isPresent();
    }

    @Override
    public Asset getAsset(AssetID identifier) {
        var resource = resources.get((ResourceKey) identifier)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + identifier));

        return toAsset(resource);
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        var resource = resources.get((ResourceKey) identifier)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + identifier));

        byte[] bytes = resources.read(resource);

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
