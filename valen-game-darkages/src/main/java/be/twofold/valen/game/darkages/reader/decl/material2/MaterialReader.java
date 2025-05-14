package be.twofold.valen.game.darkages.reader.decl.material2;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.decl.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.material.*;

import java.io.*;

public final class MaterialReader extends AbstractMaterialReader<DarkAgesAssetID, DarkAgesAsset, DarkAgesArchive> {
    public MaterialReader(DarkAgesArchive archive, DeclReader declReader) {
        super(archive, declReader);
    }

    @Override
    public boolean canRead(DarkAgesAsset resource) {
        return resource.id().type() == ResourcesType.RsStreamFile
            && resource.id().name().name().startsWith("generated/decls/material2/");
    }

    @Override
    public Material read(DataSource source, DarkAgesAsset asset) throws IOException {
        var object = declReader.read(source, asset);
        return parseMaterial(object, asset);
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
