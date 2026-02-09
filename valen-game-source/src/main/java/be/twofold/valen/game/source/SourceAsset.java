package be.twofold.valen.game.source;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public record SourceAsset(
    SourceAssetID id,
    Location location
) implements Asset {
    public SourceAsset {
        Check.nonNull(id, "id");
        Check.nonNull(location, "location");
    }

    @Override
    public AssetType type() {
        return switch (id().extension()) {
            case "vtf" -> AssetType.TEXTURE;
            case "mdl" -> AssetType.MODEL;
            default -> AssetType.RAW;
        };
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of();
    }
}
