package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;

import java.util.*;

public record DoomAsset(
    DoomAssetID id,
    AssetType type,
    long offset,
    int size,
    int sizeCompressed,
    String resourceType
) implements Asset {
    @Override
    public Map<String, Object> properties() {
        return Map.of();
    }
}
