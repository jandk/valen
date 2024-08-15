package be.twofold.valen.game.colossus;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.game.colossus.resource.*;

import java.io.*;
import java.nio.*;
import java.util.*;

final class ResourcesCollection {
    private final List<ResourcesFile> files;

    ResourcesCollection(List<ResourcesFile> files) {
        this.files = List.copyOf(files);
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
            var compressed = file.read(entry.get());
            return Decompressor
                .forType(resource.compression())
                .decompress(ByteBuffer.wrap(compressed), resource.uncompressedSize());
        }
        throw new IOException("Unknown resource: " + resource.key());
    }
}
