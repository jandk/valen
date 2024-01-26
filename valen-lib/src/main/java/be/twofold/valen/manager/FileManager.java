package be.twofold.valen.manager;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.binaryfile.*;
import be.twofold.valen.reader.compfile.*;
import be.twofold.valen.reader.decl.*;
import be.twofold.valen.reader.image.*;
import be.twofold.valen.reader.md6.*;
import be.twofold.valen.reader.md6anim.*;
import be.twofold.valen.reader.md6skl.*;
import be.twofold.valen.reader.model.*;
import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.resource.*;
import be.twofold.valen.stream.*;
import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class FileManager {
    private final PackageMapSpec spec;
    private final ResourceManager resourceManager;
    private final DeclManager declManager;
    private final Map<ResourceType, ResourceReader<?>> readers;

    public FileManager(Path base) {
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        try {
            this.resourceManager = new ResourceManager(base, spec);
            this.declManager = new DeclManager(resourceManager);

            var streamManager = new StreamManager(base, spec);
            this.readers = Map.of(
                ResourceType.Anim, new Md6AnimReader(),
                ResourceType.BaseModel, new Md6Reader(streamManager),
                ResourceType.BinaryFile, new BinaryFileReader(),
                ResourceType.CompFile, new CompFileReader(),
                ResourceType.Image, new ImageReader(streamManager),
                ResourceType.Model, new ModelReader(streamManager),
                ResourceType.Skeleton, new Md6SkeletonReader()
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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

    public JsonObject readDecl(String name) {
        return declManager.load(name);
    }

    public byte[] readRawResource(Resource entry) {
        return resourceManager.read(entry);
    }

    public <T> T readResource(FileType<T> type, Resource resource) {
        var buffer = BetterBuffer.wrap(resourceManager.read(resource));
        var result = readers.get(type.resourceType()).read(buffer, resource);
        return type.instanceType().cast(result);
    }

    public <T> T readResource(FileType<T> type, String name) {
        var entry = resourceManager.getEntry(name);
        var buffer = BetterBuffer.wrap(resourceManager.read(entry));
        var result = readers.get(type.resourceType()).read(buffer, entry);
        return type.instanceType().cast(result);
    }
}
