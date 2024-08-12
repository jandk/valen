package be.twofold.valen.game;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.reader.packagemapspec.*;
import be.twofold.valen.resource.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public final class ResourcesCollection {
    private final List<ResourcesFile> files;

    public ResourcesCollection(List<ResourcesFile> files) {
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

    public Optional<Resource> get(ResourceKey key) {
        return files.stream()
            .flatMap(f -> f.get(key).stream())
            .findFirst();
    }

    public Collection<Resource> getEntries() {
        return files.stream()
            .flatMap(file -> file.getResources().stream())
            .distinct()
            .toList();
    }

    public ByteBuffer read(Resource resource) throws IOException {
        for (var file : files) {
            var entry = file.get(resource.key());
            if (entry.isEmpty()) {
                continue;
            }
            var compressed = file.read(entry.get());
            return Decompressor
                .forType(resource.compression())
                .decompress(ByteBuffer.wrap(compressed), resource.uncompressedSize());
        }
        throw new IOException("Unknown resource: " + resource.key());
    }
}
