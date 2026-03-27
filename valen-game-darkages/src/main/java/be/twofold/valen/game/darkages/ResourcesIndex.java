package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

record ResourcesIndex(
    Map<Path, BinarySource> sources,
    Map<DarkAgesAssetID, DarkAgesAsset> index
) {
    private static final Logger log = LoggerFactory.getLogger(ResourcesIndex.class);

    ResourcesIndex {
        sources = Map.copyOf(sources);
        index = Map.copyOf(index);
    }

    static ResourcesIndex build(List<Path> paths) throws IOException {
        var sources = new HashMap<Path, BinarySource>();
        var index = new HashMap<DarkAgesAssetID, DarkAgesAsset>();
        for (var path : paths) {
            log.info("Loading Resources: {}", path);

            var source = BinarySource.open(path);
            sources.put(path, source);

            var resources = Resources.read(source);
            for (var entry : resources.entries()) {
                if (entry.uncompressedSize() > 0) {
                    var asset = mapResourceEntry(resources, entry, path);
                    index.putIfAbsent(asset.id(), asset);
                }
            }
        }

        return new ResourcesIndex(sources, index);
    }

    private static DarkAgesAsset mapResourceEntry(Resources resources, ResourcesEntry entry, Path path) {
        var name = getString(resources, entry, 1);
        var type = getString(resources, entry, 0);

        var resourceName = new ResourceName(name);
        var resourceType = ResourcesType.fromValue(type);
        var resourceKey = new DarkAgesAssetID(resourceName, resourceType, entry.variation());

        var location = new Location.FileSlice(
            path, entry.dataOffset(), Math.toIntExact(entry.dataSize())
        );
        Location finalLocation = switch (entry.compMode()) {
            case RES_COMP_MODE_NONE -> location;
            case RES_COMP_MODE_KRAKEN -> new Location.Compressed(
                location, CompressionType.OODLE, Math.toIntExact(entry.uncompressedSize())
            );
            case RES_COMP_MODE_KRAKEN_CHUNKED -> new Location.Compressed(
                new Location.FileSlice(
                    location.path(), location.offset() + 12, location.size() - 12
                ),
                CompressionType.OODLE, Math.toIntExact(entry.uncompressedSize())
            );
            default -> throw new UnsupportedOperationException(entry.compMode().toString());
        };

        return new DarkAgesAsset(
            resourceKey,
            finalLocation,
            entry.defaultHash(),
            entry.dataCheckSum(),
            entry.version()
        );
    }

    private static String getString(Resources resources, ResourcesEntry entry, int offset) {
        var i1 = Math.toIntExact(entry.strings() + offset);
        var i2 = Math.toIntExact(resources.stringIndex().get(i1));
        return resources.strings().values().get(i2);
    }
}
