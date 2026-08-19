package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.doom.megatexture.vmtr.*;
import be.twofold.valen.game.doom.vmtr.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public sealed interface DoomAsset extends Asset {
    @Override
    DoomAssetID id();

    /**
     * An entry of a {@code .resources} archive.
     */
    record Resource(
        DoomAssetID id,
        String rawType,
        Location location
    ) implements DoomAsset {
        public Resource {
            Check.nonNull(id, "id");
            Check.nonNull(rawType, "rawType");
            Check.nonNull(location, "location");
        }

        @Override
        public AssetType type() {
            return switch (rawType) {
                case "image" -> AssetType.TEXTURE;
                case "model" -> AssetType.MODEL;
                default -> AssetType.RAW;
            };
        }

        @Override
        public Map<String, Object> properties() {
            return Map.of("Type", rawType);
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }

    /**
     * One layer of one entry of a {@code .vmtr} manifest, assembled on demand from the atlas.
     */
    record Vmtr(
        DoomAssetID id,
        VmtrEntry entry
    ) implements DoomAsset {
        public Vmtr(VmtrEntry entry, VmtrLayer layer) {
            this(new DoomAssetID(entry.name(), layer), entry);
        }

        public Vmtr {
            Check.nonNull(id, "id");
            Check.nonNull(entry, "entry");
        }

        @Override
        public AssetType type() {
            return AssetType.TEXTURE;
        }

        @Override
        public Location location() {
            throw new UnsupportedOperationException("Virtual textures are assembled from pages, they have no location");
        }

        @Override
        public Map<String, Object> properties() {
            return Map.of();
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
