package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;

import java.util.*;
import java.util.stream.*;

public final class GreatCircleArchive implements Archive {
    private final Map<AssetID, Asset> commonIndex;
    private final Map<AssetID, Asset> loadedIndex;

    GreatCircleArchive(
        Map<AssetID, Asset> commonIndex,
        Map<AssetID, Asset> loadedIndex
    ) {
        this.commonIndex = Map.copyOf(commonIndex);
        this.loadedIndex = Map.copyOf(loadedIndex);
    }

    @Override
    public Optional<Asset> get(AssetID id) {
        Asset asset = loadedIndex.get(id);
        if (asset == null) {
            asset = commonIndex.get(id);
        }
        return Optional.ofNullable(asset);
    }

    @Override
    public Stream<Asset> all() {
        return loadedIndex.values().stream();
    }
}
