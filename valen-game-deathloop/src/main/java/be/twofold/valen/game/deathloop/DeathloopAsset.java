package be.twofold.valen.game.deathloop;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.deathloop.index.*;

import java.util.*;

public record DeathloopAsset(
    DeathloopAssetID id,
    IndexEntry entry
) implements Asset {
    @Override
    public AssetType type() {
        return switch (entry.typeName()) {
            case "image" -> AssetType.TEXTURE;
            default -> AssetType.RAW;
        };
    }

    @Override
    public int size() {
        return entry.uncompressedLength();
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of();
    }
}
