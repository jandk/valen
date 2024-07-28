package be.twofold.valen.manager;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.resource.*;
import be.twofold.valen.stream.*;
import jakarta.inject.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

@Singleton
public final class FileManager {
    public final ResourceManager resourceManager;
    public final StreamManager streamManager;
    private final Set<ResourceReader<?>> readers;

    private PackageMapSpec spec;

    @Inject
    FileManager(
        ResourceManager resourceManager,
        StreamManager streamManager,
        Set<ResourceReader<?>> readers
    ) {
        this.resourceManager = resourceManager;
        this.streamManager = streamManager;
        this.readers = Set.copyOf(readers);
    }

    public FileManager load(Path base) {
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        try {
            resourceManager.load(base, spec);
            streamManager.load(base, spec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    public PackageMapSpec getSpec() {
        return spec;
    }

    public boolean exists(String name, ResourceType type) {
        return resourceManager.exists(name, type);
    }

    public Collection<Resource> getEntries() {
        return resourceManager.getEntries();
    }

    public void select(String map) throws IOException {
        resourceManager.select(map);
    }

    public byte[] readRawResource(Resource resource) throws IOException {
        return Buffers.toArray(resourceManager.read(resource));
    }

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

    public boolean containsStream(long identity) {
        return streamManager.exists(identity);
    }

    public ByteBuffer readStream(long identity, int uncompressedSize) throws IOException {
        return streamManager.read(identity, uncompressedSize);
    }

    public Optional<Resource> getResource(String name, ResourceType type) {
        return resourceManager.get(name, type);
    }
}
