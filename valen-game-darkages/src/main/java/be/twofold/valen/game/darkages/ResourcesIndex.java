package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.reader.mask.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

record ResourcesIndex(
    Map<Path, BinarySource> sources,
    List<DarkAgesAsset> assets
) {
    private static final Logger log = LoggerFactory.getLogger(ResourcesIndex.class);

    ResourcesIndex {
        sources = Map.copyOf(sources);
        assets = List.copyOf(assets);
    }

    static ResourcesIndex build(List<Path> paths, ContainerMask masks) throws IOException {
        var sources = new HashMap<Path, BinarySource>();
        var assets = new ArrayList<DarkAgesAsset>();
        for (var path : paths) {
            log.info("Loading Resources: {}", path);

            var source = BinarySource.open(path);
            sources.put(path, source);

            var resources = Resources.read(source);
            // When no mask entry is present, all resources are valid.
            var maskBytes = masks.masks().get(resources.hash());
            log.warn("No mask entry for {}", resources.hash());
            var mask = maskBytes == null ? null : BitSource.little(BinarySource.wrap(maskBytes));
            for (var entry : resources.entries()) {
                if (mask != null && !mask.readFlag()) {
                    continue;
                }
                if (entry.uncompressedSize() > 0) {
                    var mappedEntry = mapResourceEntry(resources, entry, path);
                    assets.add(mappedEntry);
                }
            }
        }

        return new ResourcesIndex(sources, assets);
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
