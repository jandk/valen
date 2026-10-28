package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.idtech.game.*;

import java.util.*;

public final class EternalAsset extends IdclAsset<EternalAssetID> {
    private static final Map<ResourceType, AssetType> TYPE_MAP = Map.of(
        ResourceType.of("anim"), AssetType.ANIMATION,
        ResourceType.of("baseModel"), AssetType.MODEL,
        ResourceType.of("model"), AssetType.MODEL,
        ResourceType.of("image"), AssetType.TEXTURE,
        ResourceType.of("material2"), AssetType.MATERIAL
    );

    EternalAsset(EternalAssetID id, Location location, long hash, int version) {
        super(id, location, hash, version);
    }

    @Override
    public AssetType type() {
        if (id().path().startsWith(AssetPaths.MATERIAL2)) {
            return AssetType.MATERIAL;
        }
        return TYPE_MAP.getOrDefault(id().type(), AssetType.RAW);
    }
}
