package be.twofold.valen.game.colossus.resource;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.colossus.*;
import be.twofold.valen.game.colossus.reader.resources.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class ResourcesFile implements Container<ColossusAssetID, ColossusAsset> {
    private static final Logger log = LoggerFactory.getLogger(ResourcesFile.class);
    private final BinarySource source;
    private final Decompressor decompressor;
    private final Map<ColossusAssetID, ColossusAsset> index;

    public ResourcesFile(Path path, Decompressor decompressor) throws IOException {
        log.info("Loading Resources: {}", path);
        this.source = BinarySource.open(path);
        this.decompressor = Check.nonNull(decompressor, "decompressor");

        var resources = mapResources(Resources.read(source));
        this.index = resources.stream()
            .collect(Collectors.toUnmodifiableMap(
                ColossusAsset::id,
                Function.identity()
            ));
    }

    public Bytes read(ColossusAsset resource) throws IOException {
        source.position(resource.offset());
        return source.readBytes(resource.compressedSize());
    }

    private List<ColossusAsset> mapResources(Resources resources) {
        return resources.entries().stream()
            .map(entry -> mapResourceEntry(resources, entry))
            .toList();
    }

    private ColossusAsset mapResourceEntry(Resources resources, ResourceEntry entry) {
        var type = resources.strings().get(resources.stringIndex().get(entry.strings()/**/));
        var name = resources.strings().get(resources.stringIndex().get(entry.strings() + 1));

        var id = new ColossusAssetID(
            new ResourceName(name),
            ResourceType.valueOf(type),
            ResourceVariation.fromValue(entry.options().variation())
        );

        return new ColossusAsset(
            id,
            Math.toIntExact(entry.dataOffset()),
            Math.toIntExact(entry.dataSize()),
            Math.toIntExact(entry.options().uncompressedSize()),
            entry.options().compMode(),
            entry.options().defaultHash()
        );
    }

    @Override
    public Optional<ColossusAsset> get(ColossusAssetID key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<ColossusAsset> getAll() {
        return index.values().stream();
    }

    @Override
    public Bytes read(ColossusAssetID key, Integer size) throws IOException {
        var asset = index.get(key);
        Check.state(asset != null, () -> "Resource not found: " + key.name());

        var decompressor = switch (asset.compression()) {
            case RES_COMP_MODE_NONE -> Decompressor.none();
            case RES_COMP_MODE_KRAKEN, RES_COMP_MODE_KRAKEN_CHUNKED -> this.decompressor;
            default -> throw new UnsupportedOperationException("Unsupported compression: " + asset.compression());
        };

        source.position(asset.offset());
        var compressed = source
            .readBytes(asset.compressedSize())
            .slice(asset.compression() == ResourceCompressionMode.RES_COMP_MODE_KRAKEN_CHUNKED ? 12 : 0);
        var decompressed = decompressor.decompress(compressed, asset.size());

        // Check hash
        // long checksum = HashFunction.murmur64B(0xDEADBEEFL).hash(decompressed).asLong();
        // if (checksum != asset.checksum()) {
        //     System.err.println("Checksum mismatch! (" + checksum + " != " + asset.checksum() + ")");
        // }

        return decompressed;
    }

    @Override
    public void close() throws IOException {
        source.close();
    }
}
