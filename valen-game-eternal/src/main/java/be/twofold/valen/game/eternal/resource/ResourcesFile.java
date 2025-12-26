package be.twofold.valen.game.eternal.resource;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.resource.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.hash.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class ResourcesFile implements Container<EternalAssetID, EternalAsset> {
    private static final Logger log = LoggerFactory.getLogger(ResourcesFile.class);

    private final Map<EternalAssetID, EternalAsset> index;
    private final Decompressor decompressor;
    private final Path path;

    private BinarySource source;

    public ResourcesFile(Path path, Decompressor decompressor) throws IOException {
        log.info("Loading resources: {}", path);
        this.decompressor = Check.nonNull(decompressor, "decompressor");
        this.path = Check.nonNull(path, "path");
        this.source = BinarySource.open(path);

        var resources = mapResources(Resources.read(source));
        this.index = resources.stream()
            .collect(Collectors.toUnmodifiableMap(
                EternalAsset::id,
                Function.identity()
            ));
    }

    private List<EternalAsset> mapResources(Resources resources) {
        return resources.entries().stream()
            .map(entry -> mapResourceEntry(resources, entry))
            .toList();
    }

    private EternalAsset mapResourceEntry(Resources resources, ResourcesEntry entry) {
        var type = resources.pathStrings().get(resources.pathStringIndex().get(entry.strings()));
        var name = resources.pathStrings().get(resources.pathStringIndex().get(entry.strings() + 1));

        var resourceName = new ResourceName(name);
        var resourceType = ResourceType.fromName(type);
        var resourceVariation = ResourceVariation.fromValue(entry.variation());
        var resourceKey = new EternalAssetID(resourceName, resourceType, resourceVariation);
        return new EternalAsset(
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
    public Optional<EternalAsset> get(EternalAssetID key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<EternalAsset> getAll() {
        return index.values().stream();
    }

    @Override
    public Bytes read(EternalAssetID key, Integer size) throws IOException {
        var resource = index.get(key);
        Check.state(resource != null, () -> "Resource not found: " + key.name());

        // Get the correct decompressor first
        var decompressor = switch (resource.compression()) {
            case RES_COMP_MODE_NONE -> Decompressor.none();
            case RES_COMP_MODE_KRAKEN, RES_COMP_MODE_KRAKEN_CHUNKED -> this.decompressor;
            default -> throw new UnsupportedOperationException("Unsupported compression: " + resource.compression());
        };

        // Read the chunk
        source.position(resource.offset());
        var compressed = source
                .readBytes(resource.compressedSize())
            .slice(resource.compression() == ResourceCompressionMode.RES_COMP_MODE_KRAKEN_CHUNKED ? 12 : 0);
        var decompressed = decompressor.decompress(compressed, resource.size());

        // Check hash
        long checksum = HashFunction.murmur64B(0xDEADBEEFL).hash(decompressed).asLong();
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
