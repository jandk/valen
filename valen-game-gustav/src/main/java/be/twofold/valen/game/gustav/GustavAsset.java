package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;
import be.twofold.valen.format.granite.*;
import be.twofold.valen.game.gustav.reader.pak.*;

import java.util.*;

public sealed interface GustavAsset extends Asset {
    @Override
    GustavAssetID id();

    @Override
    default Map<String, Object> properties() {
        return Map.of();
    }

    record Pak(
        GustavAssetID id,
        PakEntry entry
    ) implements GustavAsset {
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
        GustavAssetID id,
        GraniteTexture info,
        int layer
    ) implements GustavAsset {
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
