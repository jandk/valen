package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.gustav.reader.pak.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

record PakFileIndex(
    Map<Path, BinarySource> sources,
    Map<GustavAssetID, GustavAsset> index
) {
    private static final Logger log = LoggerFactory.getLogger(PakFileIndex.class);

    PakFileIndex {
        sources = Map.copyOf(sources);
        index = Map.copyOf(index);
    }

    static PakFileIndex build(Path path) throws IOException {
        var sources = new HashMap<Path, BinarySource>();

        log.info("Loading PakFile: {}", path);

        var paths = new ArrayList<Path>();
        var source = BinarySource.open(path);

        paths.add(path);
        sources.put(path, source);

        var pak = Pak.read(source);
        List<GustavAsset> assets;
        if (pak.header().flags().contains(PakFlag.Solid)) {
            assets = mapSolid(source, pak);
        } else {
            assets = mapNormal(pak, paths, sources);
        }

        var index = assets.stream()
            .collect(Collectors.toUnmodifiableMap(GustavAsset::id, Function.identity()));
        return new PakFileIndex(sources, index);
    }

    // region Normal

    private static List<GustavAsset> mapNormal(Pak pak, List<Path> paths, Map<Path, BinarySource> sources) throws IOException {
        if (pak.header().numParts() > 1) {
            var path = paths.getFirst();
            var basename = Filenames.getBaseName(path.getFileName().toString());
            for (var i = 1; i < pak.header().numParts(); i++) {
                var resolved = path.getParent().resolve(basename + "_" + i + ".pak");
                paths.add(resolved);
                sources.put(resolved, BinarySource.open(resolved));
            }
        }

        return pak.entries().stream()
            .map(e -> mapEntry(e, paths))
            .toList();
    }

    private static GustavAsset mapEntry(PakEntry entry, List<Path> paths) {
        var id = new GustavAssetID(entry.name());
        var slice = new Location.FileSlice(paths.get(entry.archivePart()), entry.offset(), entry.compressedSize());
        var compression = mapCompression(entry.flags());
        var location = compression != CompressionType.NONE
            ? new Location.Compressed(slice, compression, entry.size())
            : slice;

        return new GustavAsset(id, location);
    }

    private static CompressionType mapCompression(Set<Compression> flags) {
        if (flags.contains(Compression.METHOD_ZLIB)) {
            return CompressionType.DEFLATE_RAW;
        } else if (flags.contains(Compression.METHOD_LZ4)) {
            return CompressionType.LZ4_BLOCK;
        } /*else if (flags.contains(Compression.METHOD_ZSTD)) {
            throw new UnsupportedOperationException("ZSTD is currently not supported");
        } */ else {
            return CompressionType.NONE;
        }
    }

    // endregion

    // region Solid

    private static List<GustavAsset> mapSolid(BinarySource source, Pak pak) throws IOException {
        var sortedEntries = pak.entries().stream()
            .sorted(Comparator.comparing(PakEntry::offset))
            .toList();

        var position = 0;
        var offsets = new HashMap<String, Integer>();
        for (var entry : sortedEntries) {
            offsets.put(entry.name(), position);
            position += entry.size();
        }

        var data = decompressSolid(source, pak.entries());
        return pak.entries().stream()
            .map(e -> mapEntrySolid(e, data, offsets.get(e.name())))
            .toList();
    }

    private static Bytes decompressSolid(BinarySource source, List<PakEntry> entries) throws IOException {
        long totalSize = 0;
        var lastOffset = Long.MIN_VALUE;
        for (var entry : entries) {
            totalSize += entry.size();
            lastOffset = Math.max(lastOffset, entry.offset() + entry.compressedSize());
        }

        var compressed = source
            .position(40) // TODO: Not hardcode
            .readBytes(Math.toIntExact(lastOffset - 40));

        return Decompressor.lz4Frame()
            .decompress(compressed, Math.toIntExact(totalSize));
    }

    private static GustavAsset mapEntrySolid(PakEntry entry, Bytes data, int offset) {
        var id = new GustavAssetID(entry.name());
        var location = new Location.InMemory(data.slice(offset, entry.size()));
        return new GustavAsset(id, location);
    }

    // endregion

}
