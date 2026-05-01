package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;

import java.util.*;
import java.util.stream.*;

final class ColossusArchive implements Archive {
    private final Map<AssetID, Asset> commonIndex;
    private final Map<AssetID, Asset> loadedIndex;

    ColossusArchive(
        Map<AssetID, Asset> commonIndex,
        Map<AssetID, Asset> loadedIndex
    ) {
        this.commonIndex = Map.copyOf(commonIndex);
        this.loadedIndex = Map.copyOf(loadedIndex);
    }

    @Override
    public Optional<Asset> get(AssetID id) {
        var asset = loadedIndex.get(id);
        if (asset == null) {
            asset = commonIndex.get(id);
        }
        return Optional.ofNullable(asset);
    }

    @Override
    public Stream<? extends Asset> all() {
        return loadedIndex.values().stream();
    }
}
