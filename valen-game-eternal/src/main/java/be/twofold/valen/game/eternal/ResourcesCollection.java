package be.twofold.valen.game.eternal;

import be.twofold.valen.game.eternal.reader.packagemapspec.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

final class ResourcesCollection {
    private final List<ResourcesFile> files;

    ResourcesCollection(List<ResourcesFile> files) {
        this.files = List.copyOf(files);
    }

    static ResourcesCollection load(Path base, PackageMapSpec spec, String name) throws IOException {
        var mapFiles = spec.mapFiles().get(name);

        var filesToLoad = new LinkedHashSet<>(mapFiles);
        filesToLoad.addAll(spec.mapFiles().get("common"));
        filesToLoad.addAll(spec.mapFiles().get("warehouse"));

        var paths = filesToLoad.stream()
            .filter(s -> s.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        var files = new ArrayList<ResourcesFile>();
        for (var path : paths) {
            files.add(new ResourcesFile(path));
        }
        return new ResourcesCollection(files);
    }

    Optional<Resource> get(ResourceKey key) {
        return files.stream()
            .flatMap(f -> f.get(key).stream())
            .findFirst();
    }

    Collection<Resource> getEntries() {
        return files.stream()
            .flatMap(file -> file.getResources().stream())
            .distinct()
            .toList();
    }

    ByteBuffer read(Resource resource) throws IOException {
        for (var file : files) {
            var entry = file.get(resource.key());
            if (entry.isEmpty()) {
                continue;
            }
            var compressed = ByteBuffer.wrap(file.read(entry.get()));
            return resource.compression()
                .decompress(compressed, resource.uncompressedSize());
        }
        throw new IOException("Unknown resource: " + resource.key());
    }
}
