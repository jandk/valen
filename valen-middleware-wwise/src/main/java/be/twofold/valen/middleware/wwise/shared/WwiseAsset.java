package be.twofold.valen.middleware.wwise.shared;

import be.twofold.valen.core.game.*;

import java.util.*;

public record WwiseAsset(
    WwiseAssetId id,
    Location location
) implements Asset {
    @Override
    public AssetType type() {
        return AssetType.AUDIO;
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of();
    }
}
