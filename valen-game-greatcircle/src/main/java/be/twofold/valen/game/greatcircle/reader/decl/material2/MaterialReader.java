package be.twofold.valen.game.greatcircle.reader.decl.material2;

import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.decl.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.reader.decl.material2.*;

public final class MaterialReader extends AbstractMaterialReader<GreatCircleAssetID, GreatCircleAsset> {
    public MaterialReader(DeclReader declReader) {
        super(declReader, IdTechGame.GREAT_CIRCLE);
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.of("material2");
    }

    @Override
    public String materialName(GreatCircleAsset asset) {
        return asset.id().path().withoutExtension().toString();
    }

    @Override
    public GreatCircleAssetID imageAssetID(String name) {
        return GreatCircleAssetID.from(name, ResourceType.of("image"));
    }

    @Override
    public GreatCircleAssetID renderParmAssetID(String name) {
        return GreatCircleAssetID.from(name, ResourceType.of("renderparm"));
    }
}
