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
    Map<Path, BinarySource> sources,
    List<EternalAsset> assets
) {
    private static final Logger log = LoggerFactory.getLogger(ResourcesIndex.class);

    ResourcesIndex {
        sources = Map.copyOf(sources);
        assets = List.copyOf(assets);
    }

    static ResourcesIndex build(List<Path> paths) throws IOException {
        var sources = new HashMap<Path, BinarySource>();
        var assets = new ArrayList<EternalAsset>();
        for (var path : paths) {
            log.info("Loading Resources: {}", path);

            var source = BinarySource.open(path);
            sources.put(path, source);

            var resources = Resources.read(source);
            for (var entry : resources.entries()) {
                if (entry.uncompressedSize() > 0) {
                    assets.add(mapResourceEntry(resources, entry, path));
                }
            }
        }

        return new ResourcesIndex(sources, assets);
    }

    private static EternalAsset mapResourceEntry(Resources resources, ResourcesEntry entry, Path path) {
        var name = getString(resources, entry, 1);
        var type = getString(resources, entry, 0);

        var resourceName = new ResourceName(name);
        var resourceType = ResourceType.fromValue(type);
        var resourceKey = new EternalAssetID(resourceName, resourceType, entry.variation());

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
