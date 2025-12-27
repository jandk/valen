package be.twofold.valen.game.gustav.pak;

import be.twofold.valen.game.gustav.*;
import be.twofold.valen.game.gustav.reader.pak.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

final class PakFileSolid extends PakFile {
    private final Map<String, Integer> offsets;
    private final Bytes content;

    PakFileSolid(Path path) throws IOException {
        Pak pak;
        Bytes content;
        try (var reader = BinarySource.open(path)) {
            pak = Pak.read(reader);
            if (!pak.header().flags().contains(PakFlag.Solid)) {
                throw new IOException("Not a solid pak file: " + path);
            }

            content = decompressSolid(reader, pak.entries());
        }

        var sortedEntries = pak.entries().stream()
            .sorted(Comparator.comparing(PakEntry::offset))
            .toList();

        var position = 0;
        var offsets = new HashMap<String, Integer>();
        for (var entry : sortedEntries) {
            offsets.put(entry.name(), position);
            position += entry.size();
        }

        this.offsets = Map.copyOf(offsets);
        this.content = content;
        super(pak.entries());
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

    @Override
    public Bytes read(GustavAssetID key, Integer size) {
        var asset = index.get(key);
        Check.state(asset != null, () -> "Resource not found: " + key.fullName());

        return content.slice(offsets.get(key.fullName()), asset.size());
    }

    @Override
    public void close() {
        // do nothing
    }
}
