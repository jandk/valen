package be.twofold.valen.game.eternal.reader.decl.material2;

import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.decl.*;
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.idtech.material.*;

public final class MaterialReader extends AbstractMaterialReader<EternalAssetID, EternalAsset> {
    public MaterialReader(DeclReader declReader) {
        super(declReader, true);
    }

    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.RsStreamFile
            && asset.id().name().name().startsWith("generated/decls/material2/");
    }

    @Override
    public String materialName(EternalAsset asset) {
        return asset.id().fullName()
            .replace("generated/decls/material2/", "")
            .replace(".decl", "");
    }

    @Override
    public EternalAssetID imageAssetID(String name) {
        return EternalAssetID.from(name, ResourceType.Image);
    }

    @Override
    public EternalAssetID renderParmAssetID(String name) {
        var fullName = "generated/decls/renderparm/" + name + ".decl";
        return EternalAssetID.from(fullName, ResourceType.RsStreamFile);
    }
}
