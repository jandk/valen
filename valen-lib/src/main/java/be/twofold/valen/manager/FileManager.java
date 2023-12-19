package be.twofold.valen.manager;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.image.*;
import be.twofold.valen.reader.md6.*;
import be.twofold.valen.reader.md6skl.*;
import be.twofold.valen.reader.model.*;
import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.resource.*;
import be.twofold.valen.stream.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class FileManager {
    private final PackageMapSpec spec;
    private final ResourceManager resourceManager;
    private final Map<ResourceType, ResourceReader<?>> readers;

    public FileManager(Path base) {
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        try {
            this.resourceManager = new ResourceManager(base, spec);

            var streamManager = new StreamManager(base, spec);
            this.readers = Map.of(
                ResourceType.Image, new ImageReader(streamManager),
                ResourceType.Model, new ModelReader(streamManager),
                ResourceType.BaseModel, new Md6Reader(streamManager),
                ResourceType.Skeleton, new Md6SkeletonReader()
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public PackageMapSpec getSpec() {
        return spec;
    }

    public void select(String map) throws IOException {
        resourceManager.select(map);
    }

    public <T> T readResource(FileType<T> type, String name) {
        var buffer = BetterBuffer.wrap(resourceManager.read(name));
        var result = readers.get(type.resourceType()).read(buffer);
        return type.instanceType().cast(result);
    }
}
