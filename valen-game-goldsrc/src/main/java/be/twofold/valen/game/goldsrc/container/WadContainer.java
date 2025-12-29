package be.twofold.valen.game.goldsrc.container;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.goldsrc.*;
import be.twofold.valen.game.goldsrc.reader.wad.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class WadContainer implements Container<GoldSrcAssetID, GoldSrcAsset> {
    private final BinarySource source;
    private final Map<GoldSrcAssetID, GoldSrcAsset> index;

    public WadContainer(Path path, String relativePath) throws IOException {
        this.source = BinarySource.open(path);

        var wad = Wad.read(source);
        this.index = wad.entries().stream()
            .map(entry -> new GoldSrcAsset.Wad(new GoldSrcAssetID(relativePath, entry.name()), entry))
            .collect(Collectors.toUnmodifiableMap(GoldSrcAsset::id, Function.identity()));
    }

    @Override
    public Optional<GoldSrcAsset> get(GoldSrcAssetID key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<GoldSrcAsset> getAll() {
        return index.values().stream();
    }

    @Override
    public Bytes read(GoldSrcAssetID key, Integer size) throws IOException {
        var asset = get(key)
            .map(GoldSrcAsset.Wad.class::cast)
            .orElseThrow(() -> new IllegalArgumentException("Unknown key: " + key));

        source.position(asset.entry().offset());
        return source.readBytes(asset.entry().size());
    }

//    @Override
//    public Bytes read(String key, Integer size) throws IOException {
//        var entry = get(key)
//            .orElseThrow(() -> new IllegalArgumentException("Unknown key: " + key));
//
//        source.position(entry.offset());
//        return source.readBytes(entry.size());
//

    /// /            var texture = switch (entry.type()) {
    /// /                case MIPTEX -> MipTex.read(source);
    /// /                case FONT -> Font.read(source);
    /// /
    /// /                default -> {
    /// /                    throw new IllegalStateException("Cant read asset " + entry.name() + " of type: " + entry.type().name());
    /// /                }
    /// /            };
    /// /            return clazz.cast(texture);
//    }
    @Override
    public void close() throws IOException {
        source.close();
    }
}
