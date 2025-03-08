package org.redeye.valen.game.spacemarines2;

import be.twofold.valen.core.game.*;

import java.util.*;

public record EmperorAsset(
    EmperorAssetId id,
    int size
) implements Asset {
    @Override
    public AssetType type() {
        var index = id.fileName().indexOf('.');
        if (index == -1) {
            return AssetType.RAW;
        }

        var extension = id.fileName().substring(index + 1);
        return switch (extension) {
            case "pct.resource" -> AssetType.TEXTURE;
            case "tpl" -> AssetType.MODEL;
            case "td" -> AssetType.DATA;
            default -> {
                if (extension.endsWith(".resource")) {
                    yield AssetType.DATA;
                }
                yield AssetType.RAW;
            }
        };
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EmperorAsset other
            && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
