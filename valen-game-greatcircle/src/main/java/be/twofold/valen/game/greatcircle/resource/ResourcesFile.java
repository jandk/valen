package be.twofold.valen.game.greatcircle.resource;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.resources.*;
import org.slf4j.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class ResourcesFile implements Container<GreatCircleAssetID, GreatCircleAsset> {
    private static final Logger log = LoggerFactory.getLogger(ResourcesFile.class);

    private final Map<GreatCircleAssetID, GreatCircleAsset> index;
    private final Decompressor decompressor;
    private final Path path;

    private BinaryReader reader;

    public ResourcesFile(Path path, Decompressor decompressor) throws IOException {
        log.info("Loading resources: {}", path);
        this.decompressor = decompressor;
        this.path = Check.notNull(path, "path");
        this.reader = BinaryReader.fromPath(path);

        var resources = mapResources(Resources.read(reader));
        this.index = resources.stream()
            .collect(Collectors.toUnmodifiableMap(
                GreatCircleAsset::id,
                Function.identity()
            ));
    }

    private List<GreatCircleAsset> mapResources(Resources resources) {
        return resources.entries().stream()
            .map(entry -> mapResourceEntry(resources, entry))
            .toList();
    }

    private GreatCircleAsset mapResourceEntry(Resources resources, ResourcesEntry entry) {
        var type = resources.pathStrings().get(resources.pathStringIndex()[entry.strings() + entry.resourceTypeString()]);
        var name = resources.pathStrings().get(resources.pathStringIndex()[entry.strings() + entry.nameString()]);

        var resourceName = new ResourceName(name);
        var resourceType = ResourceType.fromValue(type);
        var resourceVariation = ResourceVariation.fromValue(entry.variation());
        var resourceKey = new GreatCircleAssetID(resourceName, resourceType, resourceVariation);

        return new GreatCircleAsset(
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
    public Optional<GreatCircleAsset> get(GreatCircleAssetID key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<GreatCircleAsset> getAll() {
        return index.values().stream();
    }

    @Override
    public ByteBuffer read(GreatCircleAssetID key, Integer size) throws IOException {
        var resource = index.get(key);
        Check.state(resource != null, () -> "Resource not found: " + key.name());

        var decompressor = switch (resource.compression()) {
            case RES_COMP_MODE_NONE -> Decompressor.none();
            case RES_COMP_MODE_KRAKEN, RES_COMP_MODE_KRAKEN_CHUNKED, RES_COMP_MODE_LEVIATHAN -> this.decompressor;
            default -> throw new UnsupportedOperationException("Unsupported compression: " + resource.compression());
        };

        reader.position(resource.offset());
        var compressed = reader
            .readBytesStruct(resource.compressedSize())
            .slice(resource.compression() == ResourceCompressionMode.RES_COMP_MODE_KRAKEN_CHUNKED ? 12 : 0);
        return decompressor.decompress(compressed, resource.uncompressedSize()).asBuffer();
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
