package org.redeye.valen.game.source1;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public record SourceAsset(
    SourceAssetID id,
    AssetType type,
    int size,
    Map<String, Object> properties
) implements Asset {
    public SourceAsset {
        Check.notNull(id, "id");
        Check.notNull(type, "type");
        Check.argument(size >= 0, "size <= 0");
        properties = Map.copyOf(properties);
    }
}
