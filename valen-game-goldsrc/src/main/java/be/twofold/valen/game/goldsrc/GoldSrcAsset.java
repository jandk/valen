package be.twofold.valen.game.goldsrc;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.goldsrc.reader.wad.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public sealed interface GoldSrcAsset extends Asset {
    @Override
    GoldSrcAssetID id();

    @Override
    default Map<String, Object> properties() {
        return Map.of();
    }

    record File(GoldSrcAssetID id, int size) implements GoldSrcAsset {
        public File {
            Check.nonNull(id, "id");
            Check.argument(size >= 0, "size < 0");
        }

        @Override
        public AssetType type() {
            return switch (id().extension()) {
                case "mdl" -> AssetType.MODEL;
                default -> AssetType.RAW;
            };
        }
    }

    record Wad(GoldSrcAssetID id, WadEntry entry) implements GoldSrcAsset {
        public Wad {
            Check.nonNull(id, "id");
            Check.nonNull(entry, "entry");
        }

        @Override
        public AssetType type() {
            return AssetType.TEXTURE;
        }

        @Override
        public int size() {
            return entry.size();
        }
    }
}
