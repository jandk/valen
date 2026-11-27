package be.twofold.valen.game.doom.readers.staticmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class StaticModelReader implements AssetReader.Binary<Model, DoomAsset> {
    private final boolean readMaterials;

    public StaticModelReader(boolean readMaterials) {
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(DoomAsset asset) {
        return asset.id().type() == ResourceType.of("model");
    }

    @Override
    public Model read(BinaryReader reader, DoomAsset asset, LoadingContext context) throws IOException {
        var model = StaticModel.read(reader);

        var meshes = model.modelSurfaces().stream()
            .map(this::mapSurface)
            .toList();

        if (readMaterials) {
            meshes = Materials.apply(context, meshes, model.modelSurfaces(), DoomAssetID::material, StaticModelSurface::material, _ -> null);
        }

        return new Model(meshes, Axis.Z);
    }

    private Mesh mapSurface(StaticModelSurface surface) {
        return surface.geometry().toMesh(false);
    }

}
