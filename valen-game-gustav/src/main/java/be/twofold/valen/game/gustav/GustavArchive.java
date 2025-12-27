package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.export.dds.*;
import be.twofold.valen.format.granite.*;
import be.twofold.valen.game.gustav.pak.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class GustavArchive extends Archive<GustavAssetID, GustavAsset> {
    private final PakFile pakFile;
    private final Map<String, GraniteContainer> graniteContainers;
    private final Map<GustavAssetID, GustavAsset.Gts> graniteIndex;

    public GustavArchive(Path path) throws IOException {
        this.pakFile = PakFile.open(path);
        this.graniteContainers = openGraniteContainers();
        this.graniteIndex = mapGraniteAssets();
    }

    private Map<String, GraniteContainer> openGraniteContainers() throws IOException {
        var gtsAssets = pakFile.getAll()
            .filter(asset -> asset.id().fullName().endsWith(".gts"))
            .toList();

        if (gtsAssets.isEmpty()) {
            return Map.of();
        }

        var containers = new HashMap<String, GraniteContainer>();
        for (var asset : gtsAssets) {
            var source = BinarySource.wrap(read(asset.id(), null));
            var container = GraniteContainer.open(source, asset.id().fullName(), s -> BinarySource.wrap(read(new GustavAssetID(s), null)));
            containers.put(asset.id().fullName(), container);
        }
        return Map.copyOf(containers);
    }

    private Map<GustavAssetID, GustavAsset.Gts> mapGraniteAssets() {
        var assets = new HashMap<GustavAssetID, GustavAsset.Gts>();
        for (var entry : graniteContainers.entrySet()) {
            for (var texture : entry.getValue().getTextures()) {
                for (var layer = 0; layer < texture.layers().size(); layer++) {
                    var id = new GustavAssetID(entry.getKey(), texture.layers().get(layer).name());
                    assets.put(id, new GustavAsset.Gts(id, texture, layer));
                }
            }
        }
        return Map.copyOf(assets);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T loadAsset(GustavAssetID identifier, Class<T> clazz) throws IOException {
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
    public List<AssetReader<?, GustavAsset>> createReaders() {
        return List.of(
            DdsImporter.create()
        );
    }

    @Override
    public Optional<GustavAsset> get(GustavAssetID key) {
        if (key.isGts()) {
            return Optional.ofNullable(graniteIndex.get(key));
        }
        return pakFile.get(key);
    }

    @Override
    public Stream<GustavAsset> getAll() {
        return Stream.concat(
            pakFile.getAll(),
            graniteIndex.values().stream()
        );
    }

    @Override
    public Bytes read(GustavAssetID identifier, Integer size) throws IOException {
        if (identifier.isGts()) {
            throw new UnsupportedOperationException("GTS assets cannot be read directly, use loadAsset() instead");
        }
        return pakFile.read(identifier, null);
    }

    @Override
    public void close() throws IOException {
        pakFile.close();
    }
}
