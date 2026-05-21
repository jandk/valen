package be.twofold.valen.game.greatcircle.reader.decl.material2;

import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.decl.*;
import be.twofold.valen.game.greatcircle.resource.*;
import be.twofold.valen.game.idtech.material.*;

public final class MaterialReader extends AbstractMaterialReader<GreatCircleAssetID, GreatCircleAsset> {
    public MaterialReader(DeclReader declReader) {
        super(declReader, true);
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.material2;
    }

    @Override
    public String materialName(GreatCircleAsset asset) {
        return asset.id().fullName().replace(".decl", "");
    }

    @Override
    public GreatCircleAssetID imageAssetID(String name) {
        return GreatCircleAssetID.from(name, ResourceType.image);
    }

    @Override
    public GreatCircleAssetID renderParmAssetID(String name) {
        return GreatCircleAssetID.from(name, ResourceType.renderparm);
    }
}
