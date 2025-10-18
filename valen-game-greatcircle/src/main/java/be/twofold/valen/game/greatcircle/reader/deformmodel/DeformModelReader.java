package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.geometry.*;
import be.twofold.valen.game.greatcircle.resource.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.util.*;

public final class DeformModelReader implements AssetReader<Model, GreatCircleAsset> {
    private final GreatCircleArchive archive;
    private final boolean readMaterials;

    public DeformModelReader(GreatCircleArchive archive, boolean readMaterials) {
        this.archive = archive;
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.deformmodel;
    }

    @Override
    public Model read(BinaryReader reader, GreatCircleAsset asset) throws IOException {
        var deformModel = DeformModel.read(reader);
        var meshes = new ArrayList<>(readMeshes(deformModel, asset.hash()));

        if (readMaterials) {
            Materials.apply(
                archive, meshes, deformModel.meshes(),
                deformModelMesh -> deformModelMesh.lods().getFirst().materialName(),
                _ -> null
            );
        }
        return new Model(meshes, Optional.empty(), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(DeformModel deformModel, long hash) throws IOException {
        var meshes = readStreamedGeometry(deformModel, 0, hash);
        fixJointIndices(deformModel, meshes);
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(DeformModel deformModel, int lod, long hash) throws IOException {
        var uncompressedSize = deformModel.diskLayouts().get(lod).uncompressedSize();
        if (uncompressedSize == 0) {
            return List.of();
        }

        var lodInfos = deformModel.meshes().stream()
            .<LodInfo>map(mesh -> mesh.lods().get(lod))
            .toList();
        var layouts = deformModel.diskLayouts().get(lod).memoryLayouts();

        var identity = (hash << 4) | lod;
        var bytes = archive.readStream(identity, uncompressedSize);
        try (var source = BinaryReader.fromBytes(bytes)) {
            return GeometryReader.readStreamedMesh(source, lodInfos, layouts, true);
        }
    }

    private void fixJointIndices(DeformModel deformModel, List<Mesh> meshes) {
//        var jointRemap = deformModel.boneInfo().jointRemap();
//
//        // This lookup table is in reverse... Nice
//        var lookup = new byte[jointRemap.length];
//        for (var i = 0; i < jointRemap.length; i++) {
//            lookup[Short.toUnsignedInt(jointRemap[i])] = (byte) i;
//        }
//
//        for (var i = 0; i < meshes.size(); i++) {
//            var meshInfo = deformModel.meshInfos().get(i);
//            var joints = meshes.get(i)
//                .getBuffer(Semantic.JOINTS0)
//                .orElseThrow();
//
//            // Just assume it's a byte buffer, because we read it as such
//            var array = ((ByteBuffer) joints.buffer()).array();
//            for (var j = 0; j < array.length; j++) {
//                array[j] = lookup[Byte.toUnsignedInt(array[j]) + meshInfo.unknown2()];
//            }
//        }
    }
}
