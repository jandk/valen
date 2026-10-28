package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.idtech.game.*;

public final class DarkAgesAsset extends IdclAsset<DarkAgesAssetID> {
    public DarkAgesAsset(DarkAgesAssetID id, Location location, long hash, int version) {
        super(id, location, hash, version);
    }

    @Override
    public AssetType type() {
        if (id().path().startsWith(AssetPaths.MATERIAL2)) {
            return AssetType.MATERIAL;
        }
        return switch (id().type()) {
            case Anim -> AssetType.ANIMATION;
            case BaseModel, Model, StrandsHair, Vegetation -> AssetType.MODEL;
            case Image -> AssetType.TEXTURE;
            default -> AssetType.RAW;
        };
    }
}
