package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.idtech.game.*;

import java.util.*;

public final class GreatCircleAsset extends IdclAsset<GreatCircleAssetID> {
    private static final Map<ResourceType, AssetType> TYPE_MAP = Map.of(
        ResourceType.of("image"), AssetType.TEXTURE,
        ResourceType.of("basemodel"), AssetType.MODEL,
        ResourceType.of("deformmodel"), AssetType.MODEL,
        ResourceType.of("hair"), AssetType.MODEL,
        ResourceType.of("model"), AssetType.MODEL,
        ResourceType.of("material2"), AssetType.MATERIAL
    );

    GreatCircleAsset(GreatCircleAssetID id, Location location, long hash, int version) {
        super(id, location, hash, version);
    }

    @Override
    public AssetType type() {
        return TYPE_MAP.getOrDefault(id().type(), AssetType.RAW);
    }
}
