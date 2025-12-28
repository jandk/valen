package be.twofold.valen.game.gbfr;

import be.twofold.valen.core.game.*;
import be.twofold.valen.format.granite.*;
import be.twofold.valen.game.gbfr.reader.index.*;

import java.util.*;

public sealed interface GbfrAsset extends Asset {
    @Override
    GbfrAssetID id();

    @Override
    default Map<String, Object> properties() {
        return Map.of();
    }

    record Pak(
        GbfrAssetID id,
        ChunkEntry entry
    ) implements GbfrAsset {
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
    }

    record Gts(
        GbfrAssetID id,
        GraniteTexture info,
        int layer
    ) implements GbfrAsset {
        @Override
        public AssetType type() {
            return AssetType.TEXTURE;
        }

        @Override
        public int size() {
            return 0;
        }
    }
}
