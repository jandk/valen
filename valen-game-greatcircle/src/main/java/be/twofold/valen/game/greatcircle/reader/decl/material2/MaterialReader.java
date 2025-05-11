package be.twofold.valen.game.greatcircle.reader.decl.material2;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.decl.*;
import be.twofold.valen.game.greatcircle.resource.*;
import be.twofold.valen.game.idtech.material.*;

import java.io.*;

public final class MaterialReader extends AbstractMaterialReader<GreatCircleAssetID, GreatCircleAsset, GreatCircleArchive> {
    public MaterialReader(GreatCircleArchive archive, DeclReader declReader) {
        super(archive, declReader);
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.material2;
    }

    @Override
    public Material read(DataSource source, GreatCircleAsset asset) throws IOException {
        var object = declReader.read(source, asset);
        return parseMaterial(object, asset);
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
