package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;

import java.util.*;

public record DoomAsset(
    DoomAssetID id,
    Location location,
    long checksum
) implements Asset {
    @Override
    public AssetType type() {
        return AssetType.RAW;
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of();
    }
}
