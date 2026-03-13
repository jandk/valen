package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;

import java.util.*;
import java.util.stream.*;

public record GustavArchive(
    Map<AssetID, Asset> pakIndex,
    Map<AssetID, Asset> gtsIndex
) implements Archive {
    public GustavArchive {
        pakIndex = Map.copyOf(pakIndex);
        gtsIndex = Map.copyOf(gtsIndex);
    }

    @Override
    public Optional<Asset> get(AssetID id) {
        Asset asset = pakIndex.get(id);
        if (asset == null) {
            asset = gtsIndex.get(id);
        }
        return Optional.ofNullable(asset);
    }

    @Override
    public Stream<Asset> all() {
        return Stream.concat(
            pakIndex.values().stream(),
            gtsIndex.values().stream()
        );
    }
}
