package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.collect.*;

import java.util.*;

public record SoundAsset(
    WwiseAssetId id,
    Location location,
    Bytes header
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
