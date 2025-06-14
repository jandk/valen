package be.twofold.valen.game.eternal.reader.decl.material2;

import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.decl.*;
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.idtech.material.*;

public final class MaterialReader extends AbstractMaterialReader<EternalAssetID, EternalAsset, EternalArchive> {
    public MaterialReader(EternalArchive archive, DeclReader declReader) {
        super(archive, declReader, true);
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.RsStreamFile
            && resource.id().name().name().startsWith("generated/decls/material2/");
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
