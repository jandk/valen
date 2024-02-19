package be.twofold.valen.resource;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.packagemapspec.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class ResourceManager implements AutoCloseable {
    private final List<ResourcesFile> files = new ArrayList<>();
    private final Map<ResourceKey, ResourcesFile> keyIndex = new HashMap<>();
    private final NavigableMap<String, Map<ResourceKey, Resource>> nameIndex = new TreeMap<>();

    private final Path base;
    private final PackageMapSpec spec;

    public ResourceManager(Path base, PackageMapSpec spec) {
        this.base = Check.notNull(base, "base must not be null");
        this.spec = Check.notNull(spec, "spec must not be null");
    }


    public Resource get(String name, ResourceType type) {
        return get(name, type, ResourceVariation.None, Map.of(), Map.of());
    }

    public Resource get(
        String name,
        ResourceType type,
        Map<String, String> requiredAttributes,
        Map<String, String> optionalAttributes
    ) {
        return get(name, type, ResourceVariation.None, requiredAttributes, optionalAttributes);
    }

    public Resource get(
        String name,
        ResourceType type,
        ResourceVariation variation,
        Map<String, String> requiredAttributes,
        Map<String, String> optionalAttributes
    ) {
        var matches = nameIndex.subMap(
            name,
            name.substring(0, name.length() - 1) + (char) (name.charAt(name.length() - 1) + 1)
        );
        if (matches.size() == 1) {
            Map<ResourceKey, Resource> resources = matches.firstEntry().getValue();
            return match(resources, name, type, variation);
        }

        // Now we have to check the attributes
        var nameMatches = new ArrayList<String>();
        for (String match : matches.keySet()) {
            var attributes = new ResourceName(match).attributes();
            boolean attributesMatch = requiredAttributes.entrySet().stream()
                .allMatch(e -> Objects.equals(attributes.get(e.getKey()), e.getValue()));
            if (attributesMatch) {
                nameMatches.add(match);
            }
        }
        if (nameMatches.isEmpty()) {
            throw new IllegalArgumentException("No resource found with matching attributes: " + name);
        }
        if (nameMatches.size() > 1) {
            var newNameMatches = new ArrayList<String>();
            for (String match : nameMatches) {
                var attributes = new ResourceName(match).attributes();
                boolean attributesMatch = optionalAttributes.entrySet().stream()
                    .allMatch(e -> Objects.equals(attributes.get(e.getKey()), e.getValue()));
                if (attributesMatch) {
                    newNameMatches.add(match);
                }
            }

            // This is a hack
            var minmipLess = newNameMatches.stream()
                .filter(match -> !match.contains("minmip"))
                .toList();
            if (minmipLess.size() == 1) {
                return get(minmipLess.getFirst(), type, variation, requiredAttributes, optionalAttributes);
            }
            throw new IllegalArgumentException("Multiple resources found with matching attributes: " + name);
        }

        var resources = matches.firstEntry().getValue();
        return match(resources, name, type, variation);
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

        return file.read(resource.key());
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
            .computeIfAbsent(resource.name().name(), __ -> new HashMap<>())
            .putIfAbsent(key, resource);
    }

    private Resource match(Map<ResourceKey, Resource> resources, String name, ResourceType type, ResourceVariation variation) {
        return resources.values().stream()
            .filter(e -> e.type() == type && e.variation() == variation)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Resource found with wrong type or variation: " + name));
    }

    @Override
    public void close() throws IOException {
        for (var file : files) {
            file.close();
        }
        files.clear();
    }
}
