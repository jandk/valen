package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;

import java.util.*;

public record GustavAsset(
    GustavAssetID id,
    Location location
) implements Asset {
    @Override
    public AssetType type() {
        if (id.extension().equalsIgnoreCase("dds")) {
            return AssetType.TEXTURE;
        }
        return AssetType.RAW;
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of();
    }
}
