package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.dyinglight.*;
import be.twofold.valen.game.dyinglight.reader.rpack.*;

import java.io.*;
import java.util.*;

public class DLMeshReader implements AssetReader<Model, DyingLightAsset> {
    @Override
    public boolean canRead(DyingLightAsset asset) {
        return asset.id().type() == ResourceType.Mesh;
    }


    @Override
    public Model read(BinaryReader reader, DyingLightAsset asset) throws IOException {
        var meshFile = DLMeshFile.read(reader);

        var bones = new ArrayList<Bone>(meshFile.bones().size());
        for (int i = 0; i < meshFile.bones().size(); i++) {
            final DLBone dlBone = meshFile.bones().get(i);
            final Quaternion rot = dlBone.localTransform().toRotation();
            final Vector3 scl = dlBone.localTransform().toScale();
            final Vector3 pos = dlBone.localTransform().toTranslation();
            var bone = new Bone(dlBone.name(), dlBone.parentId(), rot, scl, pos, dlBone.mat1());
            bones.add(bone);
        }
        var skeleton = new Skeleton(bones, Axis.Y);
        final var vertexDataOffset = asset.sectionOffset(ResourceType.VertexData);
        final var indexDataOffset = asset.sectionOffset(ResourceType.IndexData);
        var meshes = new ArrayList<Mesh>();
        for (DLMesh dlMesh : meshFile.meshes()) {
            var lod = dlMesh.lods().getFirst();
            if (lod.subMeshes().count() == 0) continue;
            int indexOffset = 0;
            for (int i = 0; i < lod.subMeshes().count(); i++) {
                // final var mat = meshFile.materials().getMaterial(subMesh.materialId);
                var vertexSize = lod.vertexSize();
                var indexCount = lod.subMeshes().indexCounts().orElseThrow().getInt(i);

                var meshVertexDataOffset = vertexDataOffset + lod.vertexDataOffsetInVertexSection();

                List<GeoAccessor<?>> vertexAccessors = new ArrayList<>();
                var attributeOffset = 0;
                if (lod.vertexType() == 0) {
                    vertexAccessors.add(new GeoAccessor<>((int) meshVertexDataOffset + attributeOffset, lod.vertexCount(), vertexSize, VertexBufferInfo.POSITION, DLGeometry.readHalfVector3()));
                    attributeOffset += 8;
                    vertexAccessors.add(new GeoAccessor<>((int) meshVertexDataOffset + attributeOffset, lod.vertexCount(), vertexSize, VertexBufferInfo.NORMAL, DLGeometry.readByteNormal()));
                    attributeOffset += 4;
                    vertexAccessors.add(new GeoAccessor<>((int) meshVertexDataOffset + attributeOffset, lod.vertexCount(), vertexSize, VertexBufferInfo.TEX_COORDS, DLGeometry.readHalfVector2()));
                    attributeOffset += 4;
                } else {

                    vertexAccessors.add(new GeoAccessor<>((int) meshVertexDataOffset + attributeOffset, lod.vertexCount(), vertexSize, VertexBufferInfo.POSITION, DLGeometry.readPosition()));
                    attributeOffset += 12;
                    if (lod.vertexType() == 6 || lod.vertexType() == 8) {
                        vertexAccessors.add(new GeoAccessor<>((int) meshVertexDataOffset + attributeOffset, lod.vertexCount(), vertexSize, VertexBufferInfo.weights(ComponentType.FLOAT, 4), DLGeometry.readWeights()));
                        attributeOffset += 4;
                        vertexAccessors.add(new GeoAccessor<>((int) meshVertexDataOffset + attributeOffset, lod.vertexCount(), vertexSize, VertexBufferInfo.joints(ComponentType.UNSIGNED_SHORT, 4), DLGeometry.readJoints(lod.subMeshes().boneMap().orElseThrow().get(i))));
                        attributeOffset += 4;
                    }
                    vertexAccessors.add(new GeoAccessor<>((int) meshVertexDataOffset + attributeOffset, lod.vertexCount(), vertexSize, VertexBufferInfo.NORMAL, DLGeometry.readPackedI16Normals()));
                    attributeOffset += 8;
                    vertexAccessors.add(new GeoAccessor<>((int) meshVertexDataOffset + attributeOffset, lod.vertexCount(), vertexSize, VertexBufferInfo.TEX_COORDS, DLGeometry.readHalfVector2()));
                    attributeOffset += 4;
                }

                var faceAccessor = new GeoAccessor<>((int) (indexDataOffset + lod.indexDataOffsetInIndexSection()) + indexOffset * 2, indexCount, 2, VertexBufferInfo.indices(ComponentType.UNSIGNED_SHORT), DLGeometry.readFace());
                reader.position(0);
                meshes.add(new Geo(false).readMesh(reader, faceAccessor, vertexAccessors));
                // var mesh = new Mesh();
                indexOffset += indexCount;
            }
        }
        return new Model(meshes, Optional.of(skeleton), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Y);
    }
}
