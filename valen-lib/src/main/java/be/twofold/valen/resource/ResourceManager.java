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
public final class ResourceManager {
    private static final Map<ResourceType, Set<ResourceVariation>> Variations = new EnumMap<>(Map.of(
        ResourceType.HavokShape, EnumSet.of(ResourceVariation.HkMsvc64),
        ResourceType.HkNavMesh, EnumSet.of(ResourceVariation.HkMsvc64),
        ResourceType.HkNavMeshMediator, EnumSet.of(ResourceVariation.HkMsvc64),
        ResourceType.HkNavVolume, EnumSet.of(ResourceVariation.HkMsvc64),
        ResourceType.HkNavVolumeMediator, EnumSet.of(ResourceVariation.HkMsvc64),
        ResourceType.RenderProgResource, EnumSet.of(
            ResourceVariation.RenderProgVulkanPcAmd,
            ResourceVariation.RenderProgVulkanPcAmdRetail,
            ResourceVariation.RenderProgVulkanPcBase,
            ResourceVariation.RenderProgVulkanPcBaseRetail
        )
    ));

    private final List<ResourcesFile> files = new ArrayList<>();
    private final DecompressorService decompressorService;

    private Path base;
    private PackageMapSpec spec;

    @Inject
    ResourceManager(DecompressorService decompressorService) {
        this.decompressorService = decompressorService;
    }

    public void load(Path base, PackageMapSpec spec) throws IOException {
        this.base = Check.notNull(base, "base must not be null");
        this.spec = Check.notNull(spec, "spec must not be null");
    }

    public boolean exists(String name, ResourceType type) {
        return get(name, type).isPresent();
    }

    public boolean exists(ResourceKey key) {
        return get(key).isPresent();
    }

    public Optional<Resource> get(String name, ResourceType type) {
        return get(mapToKey(name, type));
    }

    public Optional<Resource> get(ResourceKey key) {
        return files.stream()
            .flatMap(f -> f.get(key).stream())
            .findFirst();
    }

    private ResourceKey mapToKey(String name, ResourceType type) {
        var variations = Variations
            .getOrDefault(type, Set.of(ResourceVariation.None));

        if (variations.size() > 1) {
            throw new IllegalArgumentException("Multiple variations found for type: " + type + " (" + variations + ")");
        }

        return new ResourceKey(
            new ResourceName(name),
            type,
            variations.iterator().next()
        );
    }


    public Collection<Resource> getEntries() {
        return files.stream()
            .flatMap(file -> file.getResources().stream())
            .distinct()
            .toList();
    }

    public byte[] read(Resource resource) throws IOException {
        var compressed = read(resource.key());
        if (compressed.length == resource.uncompressedSize()) {
            return compressed;
        }

        var decompressed = new byte[resource.uncompressedSize()];
        decompressorService.decompress(ByteBuffer.wrap(compressed), ByteBuffer.wrap(decompressed), resource.compression());
        return decompressed;
    }

    private byte[] read(ResourceKey key) throws IOException {
        for (var file : files) {
            var entry = file.get(key);
            if (entry.isPresent()) {
                return file.read(entry.get());
            }
        }
        throw new IOException("Unknown resource: " + key);
    }

    public void select(String map) throws IOException {
        var mapFiles = spec.mapFiles().get(map);
        Check.argument(mapFiles != null, () -> "Unknown map: " + map);

        mapFiles = new ArrayList<>(mapFiles);
        mapFiles.addAll(0, spec.mapFiles().get("common"));
        mapFiles.addAll(0, spec.mapFiles().get("warehouse"));

        var paths = mapFiles.stream()
            .filter(s -> s.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        for (var file : files) {
            file.close();
        }
        files.clear();
        for (var path : paths) {
            files.add(new ResourcesFile(path));
        }
    }
}
