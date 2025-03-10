package org.redeye.valen.game.source1;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import org.redeye.valen.game.source1.readers.vpk.*;

import java.util.*;

public sealed interface SourceAsset extends Asset {
    @Override
    SourceAssetID id();

    @Override
    default AssetType type() {
        return switch (id().extension()) {
            case "vtf" -> AssetType.TEXTURE;
            case "mdl" -> AssetType.MODEL;
            default -> AssetType.RAW;
        };
    }

    @Override
    default Map<String, Object> properties() {
        return Map.of();
    }

    record File(SourceAssetID id, int size) implements SourceAsset {
        public File {
            Check.notNull(id, "id");
            Check.argument(size >= 0, "size < 0");
        }
    }

    record Vpk(SourceAssetID id, VpkEntry entry) implements SourceAsset {
        public Vpk {
            Check.notNull(id, "id");
            Check.notNull(entry, "entry");
        }

        @Override
        public int size() {
            return entry.preloadBytes().length + entry.entryLength();
        }
    }
}
