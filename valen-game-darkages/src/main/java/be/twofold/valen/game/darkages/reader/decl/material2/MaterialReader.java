package be.twofold.valen.game.darkages.reader.decl.material2;

import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.decl.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.reader.decl.material2.*;

public final class MaterialReader extends AbstractMaterialReader<DarkAgesAssetID, DarkAgesAsset> {
    public MaterialReader(DeclReader declReader) {
        super(declReader, false);
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.RsStreamFile
            && asset.id().path().startsWith(AssetPaths.MATERIAL2);
    }

    @Override
    public String materialName(DarkAgesAsset asset) {
        return asset.id().path()
            .relativize(AssetPaths.MATERIAL2)
            .withoutExtension()
            .toString();
    }

    @Override
    public DarkAgesAssetID imageAssetID(String name) {
        return DarkAgesAssetID.from(name, ResourcesType.Image);
    }

    @Override
    public DarkAgesAssetID renderParmAssetID(String name) {
        var fullName = AssetPaths.RENDER_PARM.resolve(name + ".decl").toString();
        return DarkAgesAssetID.from(fullName, ResourcesType.RsStreamFile);
    }
}
