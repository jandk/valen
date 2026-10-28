package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.idtech.game.*;

public final class EternalAsset extends IdclAsset<EternalAssetID> {
    EternalAsset(EternalAssetID id, Location location, long hash, int version) {
        super(id, location, hash, version);
    }

    @Override
    public AssetType type() {
        if (id().path().startsWith(AssetPaths.MATERIAL2)) {
            return AssetType.MATERIAL;
        }
        return switch (id().type()) {
            case Anim -> AssetType.ANIMATION;
            case BaseModel, Model -> AssetType.MODEL;
            case Image -> AssetType.TEXTURE;
            default -> AssetType.RAW;
        };
    }
}
