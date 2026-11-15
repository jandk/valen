package be.twofold.valen.game.eternal.reader.decl.material2;

import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.decl.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.reader.decl.material2.*;

public final class MaterialReader extends AbstractMaterialReader<EternalAssetID, EternalAsset> {
    public MaterialReader(DeclReader declReader) {
        super(declReader, IdTechGame.ETERNAL);
    }

    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.of("rs_streamfile")
            && asset.id().path().startsWith(AssetPaths.MATERIAL2);
    }

    @Override
    public String materialName(EternalAsset asset) {
        return asset.id().path()
            .relativize(AssetPaths.MATERIAL2)
            .withoutExtension()
            .toString();
    }

    @Override
    public EternalAssetID imageAssetID(String name) {
        return EternalAssetID.from(name, ResourceType.of("image"));
    }

    @Override
    public EternalAssetID renderParmAssetID(String name) {
        var fullName = AssetPaths.RENDER_PARM.resolve(name + ".decl").toString();
        return EternalAssetID.from(fullName, ResourceType.of("rs_streamfile"));
    }
}
