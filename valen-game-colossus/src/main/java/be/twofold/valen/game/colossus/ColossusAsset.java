package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.colossus.reader.resources.*;
import be.twofold.valen.game.idtech.game.*;

public final class ColossusAsset extends IdclAsset<ColossusAssetID> {
    ColossusAsset(ColossusAssetID id, Location location, long hash, int version) {
        super(id, location, hash, version);
    }

    @Override
    public AssetType type() {
        if (id().type() == ResourcesType.Image) {
            return AssetType.TEXTURE;
        }
        return AssetType.RAW;
    }
}
