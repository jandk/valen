package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class Md6ModelReader implements AssetReader<Model, DarkAgesAsset> {
    private final DarkAgesArchive archive;

    public Md6ModelReader(DarkAgesArchive archive) {
        this.archive = archive;
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.BaseModel;
    }

    @Override
    public Model read(DataSource source, DarkAgesAsset asset) throws IOException {
        var skelName = source.readPString();
        var skeletonKey = DarkAgesAssetID.from(skelName, ResourcesType.Skeleton);
        var skeleton = archive.loadAsset(skeletonKey, Skeleton.class);
        var md6Model = Md6Model.read(source, skeleton.bones().size() + 7 & ~7);
        source.expectEnd();

        //var meshes = readMeshes(md6Model, asset.hash());
        return new Model(List.of(), Optional.of(skeleton), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(Md6Model md6Model, long hash) throws IOException {
        var meshes = readStreamedGeometry(md6Model, 0, hash);
        fixJointIndices(md6Model, meshes);
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(Md6Model md6Model, int lod, long hash) throws IOException {
        var uncompressedSize = md6Model.diskLayouts().get(lod).uncompressedSize();
        if (uncompressedSize == 0) {
            return List.of();
        }

        var lodInfos = md6Model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = md6Model.diskLayouts().get(lod).memoryLayouts();

        var identity = (hash << 4) | lod;
        var buffer = archive.readStream(identity, uncompressedSize);
        try (var source = DataSource.fromBuffer(buffer)) {
            return GeometryReader.readStreamedMesh(source, lodInfos, layouts, true);
        }
    }

    private void fixJointIndices(Md6Model md6, List<Mesh> meshes) {
        var jointRemap = md6.header().skinnedJoints();

        // This lookup table is in reverse... Nice
        var lookup = new short[jointRemap.length];
        for (var i = 0; i < jointRemap.length; i++) {
            lookup[Short.toUnsignedInt(jointRemap[i])] = (short) i;
        }

        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = md6.meshInfos().get(i);
            var joints = meshes.get(i)
                .getBuffer(Semantic.JOINTS0)
                .orElseThrow();

            // Assume it's a byte buffer, because we read it as such
            var array = ((ShortBuffer) joints.buffer()).array();
            for (var j = 0; j < array.length; j++) {
                array[j] = lookup[Short.toUnsignedInt(array[j]) + meshInfo.unknown2()];
            }
        }
    }
}
