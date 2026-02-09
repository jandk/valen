package be.twofold.valen.game.source;

import be.twofold.valen.core.game.*;

import java.util.*;
import java.util.stream.*;

public final class SourceArchive implements Archive {
    private final Map<AssetID, Asset> assets;

    public SourceArchive(Map<AssetID, Asset> assets) {
        this.assets = Map.copyOf(assets);
    }

    @Override
    public Optional<Asset> get(AssetID id) {
        return Optional.ofNullable(assets.get(id));
    }

    @Override
    public Stream<Asset> all() {
        return assets.values().stream();
    }
}
