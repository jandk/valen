//package be.twofold.valen.game.doom.resources;
//
//import be.twofold.valen.core.game.*;
//import be.twofold.valen.game.doom.*;
//import org.slf4j.*;
//import wtf.reversed.toolbox.io.*;
//
//import java.io.*;
//import java.nio.file.*;
//import java.util.*;
//
//record Index2(
//    Map<Path, BinarySource> sources,
//    Map<DoomAssetID, DoomAsset> index
//) {
//    private static final Logger log = LoggerFactory.getLogger(ResourcesIndex.class);
//
//    Index2 {
//        sources = Map.copyOf(sources);
//        index = Map.copyOf(index);
//    }
//
//    static ResourcesIndex build(List<Path> paths) throws IOException {
//        var sources = new HashMap<Path, BinarySource>();
//        var index = new HashMap<DoomAssetID, DoomAsset>();
//        for (var path : paths) {
//            log.info("Loading Resources: {}", path);
//
//            var source = BinarySource.open(path);
//            sources.put(path, source);
//
//            var resources = ResourcesIndex.read(source);
//            for (var entry : resources.entries()) {
//                if (entry.uncompressedSize() > 0) {
//                    var asset = mapResourceEntry(resources, entry, path);
//                    index.putIfAbsent(asset.id(), asset);
//                }
//            }
//        }
//
//        return new ResourcesIndex(sources, index);
//    }
//
//    private static DoomAsset mapResourceEntry(ResourcesIndex resources, ResourcesIndexEntry entry, Path path) {
//        var name = getString(resources, entry, 1);
//        var type = getString(resources, entry, 0);
//
//        var resourceName = new ResourceName(name);
//        var resourceType = ResourceType.fromValue(type);
//        var resourceKey = new DoomAssetID(resourceName, resourceType, entry.variation());
//
//        var location = new Location.FileSlice(
//            path, entry.dataOffset(), Math.toIntExact(entry.dataSize())
//        );
//        Location finalLocation = switch (entry.compMode()) {
//            case RES_COMP_MODE_NONE -> location;
//            case RES_COMP_MODE_KRAKEN -> new Location.Compressed(
//                location, CompressionType.OODLE, Math.toIntExact(entry.uncompressedSize())
//            );
//            case RES_COMP_MODE_KRAKEN_CHUNKED -> new Location.Compressed(
//                new Location.FileSlice(
//                    location.path(), location.offset() + 12, location.size() - 12
//                ),
//                CompressionType.OODLE, Math.toIntExact(entry.uncompressedSize())
//            );
//            default -> throw new UnsupportedOperationException(entry.compMode().toString());
//        };
//
//        return new DoomAsset(
//            resourceKey,
//            finalLocation,
//            entry.defaultHash(),
//            entry.dataCheckSum()
//        );
//    }
//
//    private static String getString(Resources resources, ResourcesEntry entry, int offset) {
//        var i1 = Math.toIntExact(entry.strings() + offset);
//        var i2 = Math.toIntExact(resources.stringIndex().get(i1));
//        return resources.strings().values().get(i2);
//    }
//}
