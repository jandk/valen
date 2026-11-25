package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.doom.megatexture.vmtr.*;
import be.twofold.valen.game.doom.vmtr.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.collect.*;
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
        Location location
    ) implements DoomAsset {
        private static final Map<ResourceType, AssetType> TYPE_MAP = Map.of(
            ResourceType.of("baseModel"), AssetType.MODEL,
            ResourceType.of("image"), AssetType.TEXTURE,
            ResourceType.of("model"), AssetType.MODEL
        );

        public Resource {
            Check.nonNull(id, "id");
            Check.nonNull(location, "location");
        }

        @Override
        public AssetType type() {
            return TYPE_MAP.getOrDefault(id.type(), AssetType.RAW);
        }

        @Override
        public Map<String, Object> properties() {
            return Map.of("Type", id.type());
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
            // TODO: Feels like a hack...
            return new Location.InMemory(Bytes.empty());
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
