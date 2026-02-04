package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.reader.resources.*;
import be.twofold.valen.game.eternal.resource.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

record ResourcesIndex(
    Map<FileId, BinarySource> sources,
    Map<EternalAssetID, EternalAsset> index
) {
    private static final Logger log = LoggerFactory.getLogger(ResourcesIndex.class);

    ResourcesIndex {
        sources = Map.copyOf(sources);
        index = Map.copyOf(index);
    }

    static ResourcesIndex build(Path base, List<String> paths) throws IOException {
        var sources = new HashMap<FileId, BinarySource>();
        var index = new HashMap<EternalAssetID, EternalAsset>();
        for (var path : paths) {
            log.info("Loading Resources: {}", path);

            var fileId = new FileId(path);
            var source = BinarySource.open(base.resolve(path));
            sources.put(fileId, source);

            var resources = Resources.read(source);
            for (var entry : resources.entries()) {
                if (entry.uncompressedSize() > 0) {
                    var asset = mapResourceEntry(resources, entry, fileId);
                    index.putIfAbsent(asset.id(), asset);
                }
            }
        }

        return new ResourcesIndex(sources, index);
    }

    private static EternalAsset mapResourceEntry(Resources resources, ResourcesEntry entry, FileId fileId) {
        var name = getString(resources, entry, 1);
        var type = getString(resources, entry, 0);

        var resourceName = new ResourceName(name);
        var resourceType = ResourceType.fromName(type);
        var resourceKey = new EternalAssetID(resourceName, resourceType, entry.variation());

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
        var i1 = Math.toIntExact(entry.strings() + offset);
        var i2 = Math.toIntExact(resources.stringIndex().get(i1));
        return resources.strings().values().get(i2);
    }
}
