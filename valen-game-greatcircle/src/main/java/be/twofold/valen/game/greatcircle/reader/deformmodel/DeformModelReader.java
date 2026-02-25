package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.geometry.*;
import be.twofold.valen.game.greatcircle.resource.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class DeformModelReader implements AssetReader<Model, GreatCircleAsset> {
    private final boolean readMaterials;

    public DeformModelReader(boolean readMaterials) {
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.deformmodel;
    }

    @Override
    public Model read(BinarySource source, GreatCircleAsset asset, LoadingContext context) throws IOException {
        var deformModel = DeformModel.read(source);
        var meshes = new ArrayList<>(readMeshes(deformModel, asset.hash(), context));

        if (readMaterials) {
            Materials.apply(
                context, meshes, deformModel.meshes(),
                deformModelMesh -> deformModelMesh.lods().getFirst().materialName(),
                _ -> null
            );
        }
        return new Model(meshes, Optional.empty(), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(DeformModel deformModel, long hash, LoadingContext context) throws IOException {
        var meshes = readStreamedGeometry(deformModel, 0, hash, context);
        fixJointIndices(deformModel, meshes);
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(DeformModel deformModel, int lod, long hash, LoadingContext context) throws IOException {
        var uncompressedSize = deformModel.diskLayouts().get(lod).uncompressedSize();
        if (uncompressedSize == 0) {
            return List.of();
        }

        var lodInfos = deformModel.meshes().stream()
            .<LodInfo>map(mesh -> mesh.lods().get(lod))
            .toList();
        var layouts = deformModel.diskLayouts().get(lod).memoryLayouts();

        var identity = (hash << 4) | lod;
        var bytes = context.open(new GreatCircleStreamLocation(identity, uncompressedSize));
        try (var source = BinarySource.wrap(bytes)) {
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
