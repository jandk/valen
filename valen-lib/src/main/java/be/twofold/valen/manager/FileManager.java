package be.twofold.valen.manager;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.resource.*;
import be.twofold.valen.stream.*;
import jakarta.inject.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Singleton
public final class FileManager {
    private final ResourceManager resourceManager;
    private final StreamManager streamManager;
    private final Set<ResourceReader<?>> readers;

    private PackageMapSpec spec;

    @Inject
    public FileManager(
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

    public Collection<Resource> getEntries() {
        return resourceManager.getEntries();
    }

    public void select(String map) throws IOException {
        resourceManager.select(map);
    }

    public byte[] readRawResource(Resource resource) {
        return resourceManager.read(resource);
    }

    public <T> T readResource(FileType<T> type, String name) {
        var entry = resourceManager.get(name, type.resourceType());
        var buffer = BetterBuffer.wrap(resourceManager.read(entry));
        var result = findReader(entry).read(buffer, entry);
        return type.instanceType().cast(result);
    }

    @SuppressWarnings("unchecked")
    private <T> ResourceReader<T> findReader(Resource entry) {
        return (ResourceReader<T>) readers.stream()
            .filter(r -> r.canRead(entry))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No reader found for " + entry));
    }
}
