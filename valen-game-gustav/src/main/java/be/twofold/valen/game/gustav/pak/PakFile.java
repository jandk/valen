package be.twofold.valen.game.gustav.pak;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.gustav.*;
import be.twofold.valen.game.gustav.reader.pak.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class PakFile implements Container<GustavAssetID, GustavAsset> {
    private final Map<GustavAssetID, GustavAsset> index;
    private final Map<GustavAssetID, Integer> offsets;
    private final List<BinaryReader> readers;

    public PakFile(Path path) throws IOException {
        var reader = BinaryReader.fromPath(path);
        var pak = Pak.read(reader);

        if (pak.header().flags().contains(PakFlag.Solid)) {
            var content = decompressSolid(reader, pak.entries());
            reader.close();
            reader = BinaryReader.fromBytes(content);

            var sortedEntries = pak.entries().stream()
                .sorted(Comparator.comparing(PakEntry::offset))
                .toList();

            var offsets = new HashMap<GustavAssetID, Integer>();
            int position = 0;
            for (var entry : sortedEntries) {
                offsets.put(new GustavAssetID(entry.name()), position);
                position += entry.size();
            }
            this.offsets = Map.copyOf(offsets);
        } else {
            this.offsets = Map.of();
        }

        var readers = new ArrayList<BinaryReader>();
        readers.add(reader);
        if (pak.header().numParts() > 1) {
            var basename = Filenames.getBaseName(path.getFileName().toString());
            for (var i = 1; i < pak.header().numParts(); i++) {
                readers.add(BinaryReader.fromPath(path.getParent().resolve(basename + "_" + i + ".pak")));
            }
        }
        this.readers = List.copyOf(readers);

        index = pak.entries().stream()
            .map(entry -> new GustavAsset(new GustavAssetID(entry.name()), entry))
            .collect(Collectors.toUnmodifiableMap(GustavAsset::id, Function.identity()));
    }

    private Bytes decompressSolid(BinaryReader reader, List<PakEntry> entries) throws IOException {
        long totalSize = 0;
        var firstOffset = Long.MAX_VALUE;
        var lastOffset = Long.MIN_VALUE;
        for (var entry : entries) {
            totalSize += entry.size();
            firstOffset = Math.min(firstOffset, entry.offset());
            lastOffset = Math.max(lastOffset, entry.offset() + entry.compressedSize());
        }

        var compressed = reader
            .position(40) // TODO: Not hardcode
            .readBytes(Math.toIntExact(lastOffset - 40));

        return Decompressor.lz4Frame().decompress(compressed, Math.toIntExact(totalSize));
    }

    @Override
    public Optional<GustavAsset> get(GustavAssetID key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<GustavAsset> getAll() {
        return index.values().stream();
    }

    @Override
    public Bytes read(GustavAssetID key, Integer size) throws IOException {
        var asset = index.get(key);
        Check.state(asset != null, () -> "Resource not found: " + key.name());

        if (!offsets.isEmpty()) {
            return readers.getFirst()
                .position(offsets.get(key))
                .readBytes(asset.size());
        }

        var reader = readers.get(asset.entry().archivePart());
        reader.position(asset.entry().offset());
        var compressed = reader.readBytes(asset.entry().compressedSize());

        var decompressor = getDecompressor(asset.entry().flags());
        if (decompressor == Decompressor.none()) {
            return compressed;
        }
        return decompressor
            .decompress(compressed, asset.entry().size());
    }

    private static Decompressor getDecompressor(Set<Compression> flags) {
        if (flags.contains(Compression.METHOD_ZLIB)) {
            return Decompressor.inflate(false);
        } else if (flags.contains(Compression.METHOD_LZ4)) {
            return Decompressor.lz4();
        } else if (flags.contains(Compression.METHOD_ZSTD)) {
            throw new UnsupportedOperationException("ZSTD is currently not supported");
        } else {
            return Decompressor.none();
        }
    }

    @Override
    public void close() throws IOException {
        for (var reader : readers) {
            reader.close();
        }
    }
}
