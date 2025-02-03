package be.twofold.valen.game.eternal.resource;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.hashing.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.reader.resource.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class ResourcesFile implements Container<ResourceKey, Resource> {
    private static final Logger log = LoggerFactory.getLogger(ResourcesFile.class);

    private final Map<ResourceKey, Resource> index;
    private final Decompressor decompressor;
    private final Path path;

    private DataSource source;

    public ResourcesFile(Path path, Decompressor decompressor) throws IOException {
        log.info("Loading resources: {}", path);
        this.decompressor = Check.notNull(decompressor);
        this.path = Check.notNull(path);
        this.source = DataSource.fromPath(path);

        var resources = mapResources(Resources.read(source));
        this.index = resources.stream()
            .collect(Collectors.toUnmodifiableMap(
                Resource::key,
                Function.identity()
            ));
    }

    private List<Resource> mapResources(Resources resources) {
        return resources.entries().stream()
            .map(entry -> mapResourceEntry(resources, entry))
            .toList();
    }

    private Resource mapResourceEntry(Resources resources, ResourcesEntry entry) {
        var type = resources.pathStrings().get(resources.pathStringIndex()[entry.strings()]);
        var name = resources.pathStrings().get(resources.pathStringIndex()[entry.strings() + 1]);

        var resourceName = new ResourceName(name);
        var resourceType = ResourceType.fromName(type);
        var resourceVariation = ResourceVariation.fromValue(entry.variation());
        var resourceKey = new ResourceKey(resourceName, resourceType, resourceVariation);
        return new Resource(
            resourceKey,
            entry.dataOffset(),
            entry.dataSize(),
            entry.uncompressedSize(),
            entry.compMode(),
            entry.defaultHash(),
            entry.dataCheckSum()
        );
    }

    @Override
    public Optional<Resource> get(ResourceKey key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<Resource> getAll() {
        return index.values().stream();
    }

    @Override
    public byte[] read(ResourceKey key, int uncompressedSize) throws IOException {
        var resource = index.get(key);
        Check.state(resource != null, () -> "Resource not found: " + key.name());

        // Read the chunk
        source.position(resource.offset());
        var compressed = source.readBytes(resource.compressedSize());

        // Decompress it
        var decompressor = switch (resource.compression()) {
            case RES_COMP_MODE_NONE -> Decompressor.none();
            case RES_COMP_MODE_KRAKEN, RES_COMP_MODE_KRAKEN_CHUNKED -> this.decompressor;
            default -> throw new UnsupportedOperationException("Unsupported compression: " + resource.compression());
        };
        int offset = resource.compression() == ResourceCompressionMode.RES_COMP_MODE_KRAKEN_CHUNKED ? 12 : 0;

        byte[] decompressed = new byte[resource.uncompressedSize()];
        decompressor.decompress(
            compressed, offset, compressed.length - offset,
            decompressed, 0, decompressed.length
        );

        // Check hash
        long checksum = HashFunction.murmurHash64B(0xDEADBEEFL).hash(decompressed).asLong();
        if (checksum != resource.checksum()) {
            System.err.println("Checksum mismatch! (" + checksum + " != " + resource.checksum() + ")");
        }

        return decompressed;
    }

    @Override
    public void close() throws IOException {
        if (source != null) {
            source.close();
            source = null;
        }
    }

    @Override
    public String toString() {
        return "ResourcesFile(" + path + ")";
    }
}
