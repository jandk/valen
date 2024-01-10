package be.twofold.valen.resource;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.packagemapspec.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class ResourceManager implements AutoCloseable {
    private static final Set<ResourceType> ResourceTypes = EnumSet.of(
        ResourceType.BaseModel,
        ResourceType.BinaryFile,
        ResourceType.CompFile,
        ResourceType.Image,
        ResourceType.Model,
        ResourceType.Skeleton
    );

    private final Path base;
    private final PackageMapSpec spec;
    private List<ResourcesFile> files;
    private Map<Long, ResourcesFile> hashIndex;
    private Map<String, Resource> nameIndex;

    public ResourceManager(Path base, PackageMapSpec spec) {
        this.base = Check.notNull(base, "base must not be null");
        this.spec = Check.notNull(spec, "spec must not be null");
    }

    public Collection<Resource> getEntries() {
        return files.stream()
            .flatMap(resourcesFile -> resourcesFile.getEntries().stream())
            .distinct()
            .toList();
    }

    public Resource getEntry(String name) {
        Resource resource = nameIndex.get(name);
        Check.argument(resource != null, () -> String.format("Unknown resource: %s", name));

        return getEntry(resource.hash());
    }

    public Resource getEntry(long hash) {
        ResourcesFile file = hashIndex.get(hash);
        Check.argument(file != null, () -> String.format("Unknown resource: %s", hash));

        return file.getEntry(hash);
    }

    public byte[] read(Resource entry) {
        var file = hashIndex.get(entry.hash());
        Check.argument(file != null, () -> String.format("Unknown resource: %s", entry.name()));

        return file.read(entry.hash());
    }

    public void select(String map) throws IOException {
        var mapFiles = spec.mapFiles().get(map);
        Check.argument(mapFiles != null, () -> "Unknown map: " + map);

        close();

        var paths = mapFiles.stream()
            .filter(s -> s.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        var files = new ArrayList<ResourcesFile>();
        var hashIndex = new HashMap<Long, ResourcesFile>();
        var nameIndex = new HashMap<String, Resource>();
        for (var path : paths) {
            var file = new ResourcesFile(path);
            files.add(file);
            for (var entry : file.getEntries()) {
                hashIndex.putIfAbsent(entry.hash(), file);
                if (ResourceTypes.contains(entry.type())) {
                    nameIndex.putIfAbsent(entry.name().name(), entry);
                }
            }
        }
        this.files = List.copyOf(files);
        this.hashIndex = Map.copyOf(hashIndex);
        this.nameIndex = Map.copyOf(nameIndex);
    }

    @Override
    public void close() throws IOException {
        if (files != null) {
            for (var file : files) {
                file.close();
            }
            files = null;
            hashIndex = null;
        }
    }
}
