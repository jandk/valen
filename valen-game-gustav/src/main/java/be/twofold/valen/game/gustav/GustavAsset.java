package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.gustav.reader.pak.*;

import java.util.*;

public record GustavAsset(
    GustavAssetID id,
    PakEntry entry
) implements Asset {
    @Override
    public AssetType type() {
        if (id.extension().equalsIgnoreCase("dds")) {
            return AssetType.TEXTURE;
        }
        return AssetType.RAW;
    }

    @Override
    public int size() {
        return entry().size();
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of();
    }
}
