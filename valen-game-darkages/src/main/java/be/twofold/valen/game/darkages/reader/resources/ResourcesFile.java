package be.twofold.valen.game.darkages.reader.resources;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.hashing.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.darkages.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class ResourcesFile implements Container<DarkAgesAssetID, DarkAgesAsset> {
    private static final Logger log = LoggerFactory.getLogger(ResourcesFile.class);

    private final Map<DarkAgesAssetID, DarkAgesAsset> index;
    private final Decompressor decompressor;
    private final Path path;

    private BinaryReader reader;

    public ResourcesFile(Path path, Decompressor decompressor) throws IOException {
        log.info("Loading resources: {}", path);
        this.decompressor = Check.notNull(decompressor, "decompressor");
        this.path = Check.notNull(path, "path");
        this.reader = BinaryReader.fromPath(path);

        var resources = mapResources(Resources.read(reader));
        this.index = resources.stream()
            .filter(asset -> asset.size() > 0)
            .collect(Collectors.toUnmodifiableMap(
                DarkAgesAsset::id,
                Function.identity()
            ));
    }

    private List<DarkAgesAsset> mapResources(Resources resources) {
        return resources.entries().stream()
            .map(entry -> mapResourceEntry(resources, entry))
            .toList();
    }

    private DarkAgesAsset mapResourceEntry(Resources resources, ResourcesEntry entry) {
        var type = resources.pathStrings().get(resources.pathStringIndex()[entry.strings()]);
        var name = resources.pathStrings().get(resources.pathStringIndex()[entry.strings() + 1]);

        var resourceName = new ResourceName(name);
        var resourceType = ResourcesType.fromName(type);
        var resourceVariation = ResourcesVariation.fromValue(entry.variation());
        var resourceKey = new DarkAgesAssetID(resourceName, resourceType, resourceVariation);
        return new DarkAgesAsset(
            resourceKey,
            entry.dataOffset(),
            entry.dataSize(),
            entry.uncompressedSize(),
            entry.compMode(),
            entry.defaultHash(),
            entry.dataCheckSum(),
            entry.version()
        );
    }

    @Override
    public Optional<DarkAgesAsset> get(DarkAgesAssetID key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<DarkAgesAsset> getAll() {
        return index.values().stream();
    }

    @Override
    public Bytes read(DarkAgesAssetID key, Integer size) throws IOException {
        var resource = index.get(key);
        Check.state(resource != null, () -> "Resource not found: " + key.name());

        // Get the correct decompressor first
        var decompressor = switch (resource.compression()) {
            case RES_COMP_MODE_NONE -> Decompressor.none();
            case RES_COMP_MODE_KRAKEN, RES_COMP_MODE_KRAKEN_CHUNKED -> this.decompressor;
            default -> throw new UnsupportedOperationException("Unsupported compression: " + resource.compression());
        };

        // Read the chunk
        reader.position(resource.offset());
        var compressed = reader
            .readBytesStruct(resource.compressedSize())
            .slice(resource.compression() == ResourcesCompressionMode.RES_COMP_MODE_KRAKEN_CHUNKED ? 12 : 0);
        var decompressed = decompressor.decompress(compressed, resource.size());

        // Check hash
        long checksum = HashFunction.murmurHash64B(0xDEADBEEFL).hash(decompressed).asLong();
        if (checksum != resource.checksum()) {
            System.err.println("Checksum mismatch! (" + checksum + " != " + resource.checksum() + ")");
        }

        return decompressed;
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }

    @Override
    public String toString() {
        return "ResourcesFile(" + path + ")";
    }
}
