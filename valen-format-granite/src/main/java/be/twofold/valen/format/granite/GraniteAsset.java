package be.twofold.valen.format.granite;

import be.twofold.valen.core.game.*;

import java.util.*;

public record GraniteAsset(
    GraniteAssetID id,
    String container,
    GraniteTexture texture,
    int layer
) implements Asset {
    @Override
    public AssetType type() {
        return AssetType.TEXTURE;
    }

    @Override
    public Location location() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of();
    }
}
