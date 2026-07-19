package be.twofold.valen.game.qc;

import be.twofold.valen.core.game.*;

import java.util.*;

public record QcAsset(
    QcAssetId id,
    Location location
) implements Asset {
    @Override
    public AssetType type() {
        return switch (id.extension()) {
            case "pct" -> AssetType.TEXTURE;
            default -> AssetType.RAW;
        };
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of();
    }
}
