package be.twofold.valen.game.gustav.pak;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.gustav.*;
import be.twofold.valen.game.gustav.reader.pak.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public abstract class PakFile implements Container<GustavAssetID, GustavAsset> {
    final Map<GustavAssetID, GustavAsset> index;

    public PakFile(List<PakEntry> entries) {
        this.index = entries.stream()
            .map(entry -> new GustavAsset(new GustavAssetID(entry.name()), entry))
            .collect(Collectors.toUnmodifiableMap(GustavAsset::id, Function.identity()));
    }

    public static PakFile open(Path path) throws IOException {
        try (var source = BinarySource.open(path)) {
            var header = PakHeader.read(source);
            if (header.flags().contains(PakFlag.Solid)) {
                return new PakFileSolid(path);
            } else {
                return new PakFileNormal(path);
            }
        }
    }

    @Override
    public Optional<GustavAsset> get(GustavAssetID key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<GustavAsset> getAll() {
        return index.values().stream();
    }
}
