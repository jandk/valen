package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.colossus.reader.resources.*;
import be.twofold.valen.game.colossus.resource.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

record ResourcesIndex(
    Map<Path, BinarySource> sources,
    Map<AssetID, Asset> index
) {
    private static final Logger log = LoggerFactory.getLogger(ResourcesIndex.class);

    ResourcesIndex {
        sources = Map.copyOf(sources);
        index = Map.copyOf(index);
    }

    static ResourcesIndex build(List<Path> paths) throws IOException {
        var sources = new HashMap<Path, BinarySource>();
        var index = new HashMap<AssetID, Asset>();
        for (var path : paths) {
            log.info("Loading Resources: {}", path);

            var source = BinarySource.open(path);
            sources.put(path, source);

            var resources = Resources.read(source);
            for (var entry : resources.entries()) {
                if (entry.dataSize() > 0) {
                    var asset = mapResourceEntry(resources, entry, path);
                    index.put(asset.id(), asset);
                }
            }
        }

        return new ResourcesIndex(sources, index);
    }

    private static ColossusAsset mapResourceEntry(Resources resources, ResourceEntry entry, Path path) {
        var type = resources.strings().get(resources.stringIndex().get(entry.strings()));
        var name = resources.strings().get(resources.stringIndex().get(entry.strings() + 1));

        var resourceKey = new ColossusAssetID(
            new ResourceName(name),
            ResourceType.valueOf(type),
            ResourceVariation.fromValue(entry.options().variation())
        );

        var location = new Location.FileSlice(
            path, entry.dataOffset(), entry.dataSize()
        );
        Location finalLocation = switch (entry.options().compMode()) {
            case RES_COMP_MODE_NONE -> location;
            case RES_COMP_MODE_KRAKEN -> new Location.Compressed(
                location, CompressionType.OODLE, Math.toIntExact(entry.options().uncompressedSize())
            );
            case RES_COMP_MODE_KRAKEN_CHUNKED -> new Location.Compressed(
                new Location.FileSlice(
                    location.path(), location.offset() + 12, location.size() - 12
                ),
                CompressionType.OODLE, Math.toIntExact(entry.options().uncompressedSize())
            );
            default -> throw new UnsupportedOperationException(entry.options().compMode().toString());
        };

        return new ColossusAsset(
            resourceKey,
            finalLocation,
            entry.options().defaultHash()
        );
    }
}
