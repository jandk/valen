package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.darkages.reader.geometry.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class VegetationReader implements AssetReader<Model, DarkAgesAsset> {
    private final DarkAgesArchive archive;
    private final boolean readMaterials;

    public VegetationReader(DarkAgesArchive archive, boolean readMaterials) {
        this.archive = Check.notNull(archive);
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.Vegetation;
    }

    @Override
    public Model read(DataSource source, DarkAgesAsset asset) throws IOException {
        Vegetation vegetation = Vegetation.read(source);
        source.expectEnd();

        var meshes = readMeshes(vegetation, 0, asset.hash());
        if (readMaterials) {
            Materials.apply(archive, meshes, vegetation.surfaces(), VegetationSurface::materialName, _ -> null);
        }

        return new Model(meshes, Optional.empty(), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }


    private List<Mesh> readMeshes(Vegetation vegetation, int lod, long hash) throws IOException {
        var uncompressedSize = vegetation.layouts().get(lod).uncompressedSize();
        var lodInfos = vegetation.surfaces().stream()
            .<LodInfo>map(mi -> mi.lods().get(lod))
            .toList();

        // TODO: Clean up hash method
        var key = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        key.putLong(0, hash);
        key.putInt(8, 4 - lod);
        var identity = Hash.hash(key);

        var buffer = archive.readStream(identity, uncompressedSize);

        try (var source = DataSource.fromBuffer(buffer)) {
            return GeometryReader.readStreamedMesh(source, lodInfos, true);
        }
    }
}
