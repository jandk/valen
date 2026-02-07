package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.io.*;
import be.twofold.valen.game.greatcircle.reader.resources.*;
import be.twofold.valen.game.greatcircle.resource.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

record ResourcesIndex(
    Map<FileId, BinarySource> sources,
    Map<GreatCircleAssetID, GreatCircleAsset> index
) {
    private static final Logger log = LoggerFactory.getLogger(ResourcesIndex.class);

    ResourcesIndex {
        sources = Map.copyOf(sources);
        index = Map.copyOf(index);
    }

    static ResourcesIndex build(Path base, List<String> paths) throws IOException {
        var sources = new HashMap<FileId, BinarySource>();
        var index = new HashMap<GreatCircleAssetID, GreatCircleAsset>();
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

    private static GreatCircleAsset mapResourceEntry(Resources resources, ResourcesEntry entry, FileId fileId) {
        var name = getString(resources, entry.strings() + entry.nameString());
        var type = getString(resources, entry.strings()/* + entry.typeString()*/);

        var resourceName = new ResourceName(name);
        var resourceType = ResourceType.fromValue(type);
        var resourceKey = new GreatCircleAssetID(resourceName, resourceType, entry.variation());

        var location = new Location.FileSlice(
            fileId, entry.dataOffset(), Math.toIntExact(entry.dataSize())
        );
        Location finalLocation = switch (entry.compMode()) {
            case RES_COMP_MODE_NONE -> location;
            case RES_COMP_MODE_KRAKEN,
                 RES_COMP_MODE_KRAKEN_CHUNKED,
                 RES_COMP_MODE_LEVIATHAN -> new Location.Compressed(
                location, CompressionType.OODLE, Math.toIntExact(entry.uncompressedSize())
            );
            default -> throw new UnsupportedOperationException(entry.compMode().toString());
        };

        return new GreatCircleAsset(
            resourceKey,
            finalLocation,
            entry.defaultHash(),
            entry.dataCheckSum(),
            entry.version()
        );
    }

    private static String getString(Resources resources, long index) {
        var i1 = Math.toIntExact(index);
        var i2 = Math.toIntExact(resources.stringIndex().get(i1));
        return resources.strings().values().get(i2);
    }
}
