package be.twofold.valen.format.granite;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class GraniteArchive implements Archive {
    private final StorageManager storage;
    private final Map<String, GraniteContainer> containers;
    private final Map<GraniteAssetID, GraniteAsset> assets;

    public GraniteArchive(List<Asset> gtsAssets, StorageManager storage, GtpSupplier gtpSupplier) throws IOException {
        this.storage = Check.nonNull(storage, "storageManager");
        this.containers = openGraniteContainers(gtsAssets, gtpSupplier);
        this.assets = mapGraniteAssets();
    }

    public Map<String, GraniteContainer> getContainers() {
        return containers;
    }

    private Map<String, GraniteContainer> openGraniteContainers(List<Asset> gtsAssets, GtpSupplier gtpSupplier) throws IOException {
        if (gtsAssets.isEmpty()) {
            return Map.of();
        }

        var containers = new HashMap<String, GraniteContainer>();
        for (var asset : gtsAssets) {
            var source = BinarySource.wrap(storage.open(asset.location()));
            var container = GraniteContainer.open(source, asset.id().fullName(), gtpSupplier);
            containers.put(asset.id().fullName(), container);
        }
        return Map.copyOf(containers);
    }

    private Map<GraniteAssetID, GraniteAsset> mapGraniteAssets() {
        var assets = new HashMap<GraniteAssetID, GraniteAsset>();
        for (var entry : containers.entrySet()) {
            for (var texture : entry.getValue().getTextures()) {
                for (var layer = 0; layer < texture.layers().size(); layer++) {
                    var id = new GraniteAssetID(entry.getKey(), texture.layers().get(layer).name());
                    assets.put(id, new GraniteAsset(id, entry.getKey(), texture, layer));
                }
            }
        }
        return Map.copyOf(assets);
    }

    @Override
    public Optional<Asset> get(AssetID id) {
        if (id instanceof GraniteAssetID graniteAssetID) {
            return Optional.ofNullable(assets.get(graniteAssetID));
        }
        return Optional.empty();
    }

    @Override
    public Stream<Asset> all() {
        return assets.values().stream()
            .map(Function.identity());
    }
}
