package be.twofold.valen.game.darkages.reader.decl.material2;

import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.decl.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.material.*;

public final class MaterialReader extends AbstractMaterialReader<DarkAgesAssetID, DarkAgesAsset, DarkAgesArchive> {
    public MaterialReader(DarkAgesArchive archive, DeclReader declReader) {
        super(archive, declReader, false);
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.RsStreamFile
            && asset.id().name().name().startsWith("generated/decls/material2/");
    }

    @Override
    public String materialName(DarkAgesAsset asset) {
        return asset.id().fullName()
            .replace("generated/decls/material2/", "")
            .replace(".decl", "");
    }

    @Override
    public DarkAgesAssetID imageAssetID(String name) {
        return DarkAgesAssetID.from(name, ResourcesType.Image);
    }

    @Override
    public DarkAgesAssetID renderParmAssetID(String name) {
        var fullName = "generated/decls/renderparm/" + name + ".decl";
        return DarkAgesAssetID.from(fullName, ResourcesType.RsStreamFile);
    }
}
