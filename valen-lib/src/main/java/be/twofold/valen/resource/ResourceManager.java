package be.twofold.valen.resource;

import be.twofold.valen.compression.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.packagemapspec.*;
import jakarta.inject.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

@Singleton
public final class ResourceManager implements AutoCloseable {
    private final List<ResourcesFile> files = new ArrayList<>();
    private final Map<ResourceKey, ResourcesFile> keyIndex = new HashMap<>();
    private final Map<String, Map<ResourceKey, Resource>> nameIndex = new TreeMap<>();

    private final DecompressorService decompressorService;

    private Path base;
    private PackageMapSpec spec;

    @Inject
    public ResourceManager(DecompressorService decompressorService) {
        this.decompressorService = decompressorService;
    }

    public void load(Path base, PackageMapSpec spec) throws IOException {
        this.base = Check.notNull(base, "base must not be null");
        this.spec = Check.notNull(spec, "spec must not be null");
    }

    public boolean exists(String name, ResourceType type) {
        var map = nameIndex.get(name);
        return map != null && map.values().stream()
            .anyMatch(r -> r.type() == type);
    }

    public Resource get(String name, ResourceType type) {
        return get(name, type, ResourceVariation.None);
    }

    public Resource get(
        String name,
        ResourceType type,
        ResourceVariation variation
    ) {
        var matches = nameIndex.get(name.toLowerCase());
        if (matches == null) {
            // Sometimes files are straight up missing
            return null;
        }
        return matches.values().stream()
            .filter(e -> e.type() == type && e.variation() == variation)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Resource found with wrong type or variation: " + name));
    }


    public Collection<Resource> getEntries() {
        return files.stream()
            .flatMap(file -> file.getResources().stream())
            .distinct()
            .toList();
    }


    public byte[] read(Resource resource) {
        var file = keyIndex.get(resource.key());
        Check.argument(file != null, () -> "Unknown resource: " + resource.key());

        try {
            var compressed = file.read(resource.key());
            var decompressed = new byte[resource.uncompressedSize()];
            decompressorService.decompress(ByteBuffer.wrap(compressed), ByteBuffer.wrap(decompressed), resource.compression());
            return decompressed;
        } catch (IOException e) {
            System.out.println("Error reading resource: " + resource.key());
            throw new UncheckedIOException(e);
        }
    }

    public void select(String map) throws IOException {
        var mapFiles = spec.mapFiles().get(map);
        Check.argument(mapFiles != null, () -> "Unknown map: " + map);

        close();
        mapFiles = new ArrayList<>(mapFiles);
        mapFiles.addAll(0, spec.mapFiles().get("common"));
        mapFiles.addAll(0, spec.mapFiles().get("warehouse"));

        var paths = mapFiles.stream()
            .filter(s -> s.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        files.clear();
        keyIndex.clear();
        nameIndex.clear();

        for (var path : paths) {
            ResourcesFile file = new ResourcesFile(path);

            files.add(file);
            for (Resource resource : file.getResources()) {
                index(file, resource);
            }
        }

        nameIndex.replaceAll((key, value) -> Map.copyOf(value));
    }


    private void index(ResourcesFile file, Resource resource) {
        var key = resource.key();
        keyIndex.putIfAbsent(key, file);
        nameIndex
            .computeIfAbsent(resource.nameString(), __ -> new HashMap<>())
            .putIfAbsent(key, resource);
    }

    @Override
    public void close() throws IOException {
        for (var file : files) {
            file.close();
        }
        files.clear();
    }
}
