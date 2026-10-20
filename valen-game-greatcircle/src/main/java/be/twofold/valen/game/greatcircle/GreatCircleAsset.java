package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.idtech.game.*;

public final class GreatCircleAsset extends IdclAsset<GreatCircleAssetID> {
    GreatCircleAsset(GreatCircleAssetID id, Location location, long hash, int version) {
        super(id, location, hash, version);
    }

    @Override
    public AssetType type() {
        return switch (id().type()) {
            case image -> AssetType.TEXTURE;
            case basemodel, deformmodel, hair, model -> AssetType.MODEL;
            case material2 -> AssetType.MATERIAL;
            default -> AssetType.RAW;
        };
    }
}
