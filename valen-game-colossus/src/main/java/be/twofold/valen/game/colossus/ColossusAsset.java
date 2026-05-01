package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.colossus.resource.*;

import java.util.*;

public record ColossusAsset(
    ColossusAssetID id,
    Location location,
    long hash
) implements Asset {
    @Override
    public AssetType type() {
        if (id.type() == ResourceType.image) {
            return AssetType.TEXTURE;
        }
        return AssetType.RAW;
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of();
    }
}
