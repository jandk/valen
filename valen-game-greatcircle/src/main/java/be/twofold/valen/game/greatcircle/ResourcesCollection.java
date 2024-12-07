package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.hashing.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.greatcircle.reader.resources.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

final class ResourcesCollection {
    private final List<ResourcesFile> files;
    private final Decompressor decompressor;

    ResourcesCollection(List<ResourcesFile> files, Decompressor decompressor) {
        this.files = List.copyOf(files);
        this.decompressor = Check.notNull(decompressor);
    }

    static ResourcesCollection load(List<Path> paths, Decompressor decompressor) throws IOException {
        var files = new ArrayList<ResourcesFile>();
        for (var path : paths) {
            files.add(new ResourcesFile(path));
        }
        return new ResourcesCollection(files, decompressor);
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

    byte[] read(Resource resource) throws IOException {
        for (var file : files) {
            var entry = file.get(resource.key());
            if (entry.isEmpty()) {
                continue;
            }

            var decompressor = switch (resource.compression()) {
                case RES_COMP_MODE_NONE -> Decompressor.none();
                case RES_COMP_MODE_KRAKEN, RES_COMP_MODE_KRAKEN_CHUNKED -> this.decompressor;
                default ->
                    throw new UnsupportedOperationException("Unsupported compression: " + resource.compression());
            };
            int offset = resource.compression() == ResourceCompressionMode.RES_COMP_MODE_KRAKEN_CHUNKED ? 12 : 0;

            var compressed = file.read(entry.get());
            byte[] decompressed = new byte[resource.uncompressedSize()];
            decompressor.decompress(
                compressed, offset, compressed.length - offset,
                decompressed, 0, decompressed.length
            );

//            long checksum = HashFunction.murmurHash64B(0xDEADBEEFL).hash(decompressed).asLong();
//            if (checksum != entry.get().checksum()) {
//                System.err.println("Checksum mismatch! (" + checksum + " != " + entry.get().checksum() + ")");
//            }
            return decompressed;
        }
        throw new IOException("Unknown resource: " + resource.key());
    }
}
