package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;

import java.util.*;

public record DoomAsset(
    DoomAssetID id,
    String rawType,
    Location location
) implements Asset {
    @Override
    public AssetType type() {
        switch (rawType) {
            case "image":
                return AssetType.TEXTURE;
            case "model":
                return AssetType.MODEL;
            case "baseModel":
            case "skeleton":
            case "material":
            case "decalatlas":
            case "transsortatlas":
            case "anim":
                return AssetType.RAW;
            default:
                return AssetType.RAW;
        }
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of("Type", rawType);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
