package be.twofold.valen.game;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.decl.*;
import be.twofold.valen.reader.decl.material2.*;
import be.twofold.valen.reader.decl.renderparm.*;
import be.twofold.valen.reader.image.*;
import be.twofold.valen.reader.md6model.*;
import be.twofold.valen.reader.md6skel.*;
import be.twofold.valen.reader.staticmodel.*;
import be.twofold.valen.resource.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class EternalArchive implements Archive<ResourceKey> {
    private final StreamDbCollection streams;
    private final ResourcesCollection resources;
    private final List<ResourceReader<?>> readers;

    public EternalArchive(StreamDbCollection streams, ResourcesCollection resources) {
        this.streams = Objects.requireNonNull(streams);
        this.resources = Objects.requireNonNull(resources);

        var declReader = new DeclReader(this);
        this.readers = List.of(
            declReader,
            new ImageReader(this),
            new MaterialReader(this, declReader),
            new Md6ModelReader(this),
            new Md6SkelReader(),
            new RenderParmReader(),
            new StaticModelReader(this)
        );
    }

    @Override
    public List<Asset<ResourceKey>> assets() {
        return resources.getEntries().stream()
            .map(this::toAsset)
            .distinct()
            .sorted()
            .toList();
    }

    private Asset<ResourceKey> toAsset(Resource resource) {
        return new Asset<>(
            resource.key(),
            mapType(resource.type()),
            resource.uncompressedSize(),
            Map.of("hash", resource.hash())
        );
    }

    @Override
    public boolean exists(ResourceKey identifier) {
        return resources.get(identifier).isPresent();
    }

    @Override
    public Object loadAsset(ResourceKey identifier) throws IOException {
        var resource = resources.get(identifier)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + identifier));

        var reader = readers.stream()
            .filter(r -> r.canRead(resource.key()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No reader found for resource: " + resource));

        var buffer = resources.read(resource);
        try (var source = ByteArrayDataSource.fromBuffer(buffer)) {
            return reader.read(source, toAsset(resource));
        }
    }

    @Override
    public ByteBuffer loadRawAsset(ResourceKey identifier) throws IOException {
        var resource = resources.get(identifier)
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + identifier));

        return resources.read(resource);
    }

    public boolean containsStream(long identifier) {
        return streams.exists(identifier);
    }

    public ByteBuffer readStream(long identifier, int uncompressedSize) throws IOException {
        return streams.read(identifier, uncompressedSize);
    }

    private AssetType mapType(ResourceType type) {
        return switch (Objects.requireNonNull(type)) {
            case Image -> AssetType.Image;
            default -> AssetType.Binary;
        };
    }

    /*
        public <T> T readResource(String name, FileType<T> type) throws IOException {
        var entry = resourceManager.get(name, type.resourceType())
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + name));

        var reader = readers.stream()
            .filter(r -> r.canRead(entry))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No reader found for " + entry));

        // TODO: Fix this, a filter would fix issues with decl sub readers
        var readType = reader.getReadType();
        if (readType != null && !readType.isAssignableFrom(type.instanceType())) {
            throw new IllegalArgumentException("Reader " + reader.getClass() + " cannot read " + type.instanceType());
        }

        var buffer = resourceManager.read(entry);
        var result = reader.read(ByteArrayDataSource.fromBuffer(buffer), entry);
        return type.instanceType().cast(result);
    }
     */
}
