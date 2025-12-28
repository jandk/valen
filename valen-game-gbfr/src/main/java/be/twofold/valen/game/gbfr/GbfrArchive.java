package be.twofold.valen.game.gbfr;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.format.granite.*;
import be.twofold.valen.game.gbfr.reader.index.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class GbfrArchive extends Archive<GbfrAssetID, GbfrAsset> {
    private final Map<Integer, BinarySource> cachedChunks = new HashMap<>();

    private final Index index;
    private final List<BinarySource> sources;
    private final Map<String, GraniteContainer> graniteContainers;
    private final Map<GbfrAssetID, GbfrAsset.Gts> graniteIndex;

    public GbfrArchive(Path indexPath) throws IOException {
        this.index = Index.load(indexPath);
        this.sources = openSources(indexPath.getParent());
        this.graniteContainers = openGraniteContainers();
        this.graniteIndex = mapGraniteAssets();
    }

    private List<BinarySource> openSources(Path basePath) throws IOException {
        var sources = new ArrayList<BinarySource>();
        for (var i = 0; i < index.numArchives(); i++) {
            var path = basePath.resolve("data." + i);
            sources.add(BinarySource.open(path));
        }
        return List.copyOf(sources);
    }

    private Map<String, GraniteContainer> openGraniteContainers() throws IOException {
        var containers = new HashMap<String, GraniteContainer>();
        for (var r : List.of("2k", "4k")) {
            for (var i = 0; i <= 11; i++) {
                var name = "granite/" + r + "/gts/" + i + "/" + i + ".gts";
                var source = BinarySource.wrap(read(new GbfrAssetID(name), null));
                var container = GraniteContainer.open(source, name, s -> BinarySource.wrap(read(new GbfrAssetID(s), null)));
                containers.put(name, container);
            }
        }
        return Map.copyOf(containers);
    }

    private Map<GbfrAssetID, GbfrAsset.Gts> mapGraniteAssets() {
        var assets = new HashMap<GbfrAssetID, GbfrAsset.Gts>();
        for (var entry : graniteContainers.entrySet()) {
            for (var texture : entry.getValue().getTextures()) {
                for (var layer = 0; layer < texture.layers().size(); layer++) {
                    var id = new GbfrAssetID(entry.getKey(), texture.layers().get(layer).name());
                    assets.put(id, new GbfrAsset.Gts(id, texture, layer));
                }
            }
        }
        return Map.copyOf(assets);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T loadAsset(GbfrAssetID identifier, Class<T> clazz) throws IOException {
        if (!identifier.isGts()) {
            return super.loadAsset(identifier, clazz);
        }
        if (clazz != Texture.class) {
            throw new UnsupportedOperationException("Only Texture is supported for GTS assets");
        }

        var asset = graniteIndex.get(identifier);
        return (T) graniteContainers
            .get(asset.id().pakFile())
            .read(asset.info(), asset.layer());
    }

    @Override
    public List<AssetReader<?, GbfrAsset>> createReaders() {
        return List.of();
    }

    @Override
    public Optional<GbfrAsset> get(GbfrAssetID key) {
        if (key.isGts()) {
            return Optional.ofNullable(graniteIndex.get(key));
        }
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Stream<GbfrAsset> getAll() {
        return (Stream) graniteIndex.values().stream();
    }

    @Override
    public Bytes read(GbfrAssetID identifier, Integer size) throws IOException {
        if (identifier.isGts()) {
            throw new UnsupportedOperationException("GTS assets cannot be read directly, use loadAsset() instead");
        }

        var entryIndex = index.getIndex(identifier.fullName())
            .orElseThrow(() -> new FileNotFoundException("Unknown asset: " + identifier));
        var chunkIndex = index.fileEntries().get(entryIndex);
        var chunkEntry = index.chunkEntries().get(chunkIndex.chunk());

        if (index.cachedChunks().contains(chunkIndex.chunk())) {
            if (!cachedChunks.containsKey(chunkIndex.chunk())) {
                var compressed = sources.get(chunkEntry.fileId())
                    .position(chunkEntry.offset())
                    .readBytes(chunkEntry.compressedSize());
                var decompressed = Decompressor.lz4Block().decompress(compressed, chunkEntry.size());
                cachedChunks.put(chunkIndex.chunk(), BinarySource.wrap(decompressed));
            }
            return cachedChunks.get(chunkIndex.chunk())
                .position(chunkIndex.offset())
                .readBytes(chunkIndex.length());
        }

        return sources.get(chunkEntry.fileId())
            .position(chunkEntry.offset() + chunkIndex.offset())
            .readBytes(chunkIndex.length());
    }

    @Override
    public void close() throws IOException {
        for (var source : sources) {
            source.close();
        }
    }
}
