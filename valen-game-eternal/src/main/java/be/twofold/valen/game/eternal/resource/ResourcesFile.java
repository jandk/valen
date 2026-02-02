package be.twofold.valen.game.eternal.resource;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.resource.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class ResourcesFile implements Closeable {
    private static final Logger log = LoggerFactory.getLogger(ResourcesFile.class);

    private final Map<EternalAssetID, EternalAsset> index;
    private final Path path;

    private BinarySource source;

    public ResourcesFile(Path path, FileId fileId) throws IOException {
        log.info("Loading resources: {}", path);
        this.path = Check.nonNull(path, "path");
        this.source = BinarySource.open(path);

        var resources = mapResources(Resources.read(source), fileId);
        this.index = resources.stream()
            .collect(Collectors.toUnmodifiableMap(
                EternalAsset::id,
                Function.identity()
            ));
    }

    private List<EternalAsset> mapResources(Resources resources, FileId fileId) {
        return resources.entries().stream()
            .map(entry -> mapResourceEntry(resources, entry, fileId))
            .toList();
    }

    private EternalAsset mapResourceEntry(Resources resources, ResourcesEntry entry, FileId fileId) {
        var name = getString(resources, entry, 1);
        var type = getString(resources, entry, 0);

        var resourceName = new ResourceName(name);
        var resourceType = ResourceType.fromName(type);
        var resourceVariation = ResourceVariation.fromValue(entry.variation());
        var resourceKey = new EternalAssetID(resourceName, resourceType, resourceVariation);

        var location = new StorageLocation.FileSlice(
            fileId, entry.dataOffset(), Math.toIntExact(entry.dataSize())
        );
        StorageLocation finalLocation = switch (entry.compMode()) {
            case RES_COMP_MODE_NONE -> location;
            case RES_COMP_MODE_KRAKEN -> new StorageLocation.Compressed(
                location, "oodle", Math.toIntExact(entry.uncompressedSize())
            );
            case RES_COMP_MODE_KRAKEN_CHUNKED -> new StorageLocation.Compressed(
                new StorageLocation.FileSlice(
                    location.fileId(), location.offset() + 12, location.size() - 12
                ),
                "oodle", Math.toIntExact(entry.uncompressedSize())
            );
            default -> throw new UnsupportedOperationException(entry.compMode().toString());
        };

        return new EternalAsset(
            resourceKey,
            finalLocation,
            entry.defaultHash(),
            entry.dataCheckSum()
        );
    }

    private static String getString(Resources resources, ResourcesEntry entry, int offset) {
        int i1 = Math.toIntExact(entry.strings() + offset);
        int i2 = Math.toIntExact(resources.stringIndex().get(i1));
        return resources.strings().values().get(i2);
    }

    public Stream<EternalAsset> getAll() {
        return index.values().stream();
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
