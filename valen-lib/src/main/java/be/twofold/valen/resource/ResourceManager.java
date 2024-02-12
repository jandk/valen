package be.twofold.valen.resource;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.packagemapspec.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class ResourceManager implements AutoCloseable {
    private static final Set<ResourceType> ResourceTypes = EnumSet.of(
        ResourceType.Anim,
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
    private Map<ResourceKey, ResourcesFile> index;
    private Map<String, Map<ResourceKey, Resource>> names;

    public ResourceManager(Path base, PackageMapSpec spec) {
        this.base = Check.notNull(base, "base must not be null");
        this.spec = Check.notNull(spec, "spec must not be null");
    }

    public Collection<Resource> getEntries() {
        return files.stream()
            .flatMap(file -> file.getResources().stream())
            .distinct()
            .toList();
    }

    public Resource getEntry(String name) {
        var resources = names.get(name);
        Check.argument(resources != null, () -> String.format("Unknown resource: %s", name));

        // TODO: handle multiple resources with the same name
        ResourceKey resource = resources.keySet().iterator().next();
        var file = index.get(resource);
        Check.argument(file != null, () -> String.format("Unknown resource: %s", resource));

        return file.getEntry(resource);
    }

    public byte[] read(Resource resource) {
        var file = index.get(resource.key());
        Check.argument(file != null, () -> String.format("Unknown resource: %s", resource.key()));

        return file.read(resource.key());
    }

    public void select(String map) throws IOException {
        var mapFiles = spec.mapFiles().get(map);
        Check.argument(mapFiles != null, () -> "Unknown map: " + map);

        close();
        mapFiles = new ArrayList<>(mapFiles);
        mapFiles.addAll(0,spec.mapFiles().get("common"));
        mapFiles.addAll(0,spec.mapFiles().get("warehouse"));

        var paths = mapFiles.stream()
            .filter(s -> s.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        var files = new ArrayList<ResourcesFile>();
        var index = new HashMap<ResourceKey, ResourcesFile>();
        var names = new HashMap<String, Map<ResourceKey, Resource>>();

        for (var path : paths) {
            var file = new ResourcesFile(path);
            files.add(file);
            file.getResources()
                .forEach(e -> {
                    var key = new ResourceKey(e.name(), e.type(), e.variation());
                    index.putIfAbsent(key, file);
                    names
                        .computeIfAbsent(e.name().name(), __ -> new HashMap<>())
                        .putIfAbsent(key, e);
                });
        }

        names.replaceAll((key, value) -> Map.copyOf(value));
        this.files = List.copyOf(files);
        this.index = Map.copyOf(index);
        this.names = Map.copyOf(names);
    }

    @Override
    public void close() throws IOException {
        if (files != null) {
            for (var file : files) {
                file.close();
            }
            files = null;
            index = null;
            names = null;
        }
    }
}
