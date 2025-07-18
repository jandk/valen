package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.darkages.reader.geometry.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.stream.*;

public final class Md6ModelReader implements AssetReader<Model, DarkAgesAsset> {
    private final DarkAgesArchive archive;
    private final boolean readMaterials;

    public Md6ModelReader(DarkAgesArchive archive, boolean readMaterials) {
        this.archive = archive;
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.BaseModel;
    }

    @Override
    public Model read(BinaryReader reader, DarkAgesAsset asset) throws IOException {
        var skelName = reader.readPString();
        var skeletonKey = DarkAgesAssetID.from(skelName, ResourcesType.Skeleton);
        var skeleton = archive.loadAsset(skeletonKey, Skeleton.class);
        var md6Model = Md6Model.read(reader, skeleton.bones().size() + 7 & ~7);
        reader.expectEnd();

        var meshes = readMeshes(md6Model, 0, asset.hash());
        if (readMaterials) {
            Materials.apply(archive, meshes, md6Model.meshInfos(), Md6ModelMeshInfo::materialName, Md6ModelMeshInfo::meshName);
        }

        return new Model(meshes, Optional.of(skeleton), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(Md6Model md6Model, int lod, long hash) throws IOException {
        if (md6Model.diskLayouts().isEmpty()) {
            return List.of();
        }

        var uncompressedSize = md6Model.diskLayouts().get(lod).uncompressedSize();
        if (uncompressedSize == 0) {
            return List.of();
        }

        var lodInfos = md6Model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();

        var identity = Hash.hash(hash, 4 - lod, 0);
        var buffer = archive.readStream(identity, uncompressedSize);

        try (var source = BinaryReader.fromBuffer(buffer)) {
            List<Mesh> meshes = GeometryReader.readStreamedMesh(source, lodInfos, true);
            meshes = mergeJointsAndWeights(md6Model, meshes);
            fixJointIndices(md6Model, meshes);
            return meshes;
        }
    }

    private ArrayList<Mesh> mergeJointsAndWeights(Md6Model md6, List<Mesh> meshes) {
        var newMeshes = new ArrayList<Mesh>();
        for (int m = 0; m < meshes.size(); m++) {
            int influence = md6.meshInfos().get(m).lodInfos().getFirst().influence();
            newMeshes.add(mergeJointsAndWeights(meshes.get(m), influence));
        }
        return newMeshes;
    }

    private static Mesh mergeJointsAndWeights(Mesh mesh, int realInfluence) {
        var weightBuffers = mesh.getBuffers(Semantic.WEIGHTS);
        if (weightBuffers.isEmpty() || realInfluence == 1) {
            return mesh;
        }

        var influence = weightBuffers.stream()
            .mapToInt(vb -> vb.info().size())
            .sum() + 1;

        int count = weightBuffers.getFirst().count();
        var joints = (ShortBuffer) mesh.getBuffer(Semantic.JOINTS).orElseThrow().buffer();
        if (influence != realInfluence) {
            for (int i = 0, o = 0, lim = joints.limit(); i < lim; i += influence, o += realInfluence) {
                for (int j = 0; j < realInfluence; j++) {
                    joints.put(o + j, joints.get(i + j));
                }
            }
            joints.limit(count * realInfluence);
        }

        var buffer1 = (FloatBuffer) weightBuffers.getFirst().buffer();
        var buffer2 = (FloatBuffer) weightBuffers.getLast().buffer();
        var weights = FloatBuffer.allocate(realInfluence * count);

        var localInfluence = new float[realInfluence];
        for (int c = 0; c < count; c++) {
            for (int i = 1; i < influence - 3; i++) {
                localInfluence[i] = buffer2.get();
            }
            for (int i = influence - 3; i < realInfluence; i++) {
                localInfluence[i] = buffer1.get();
            }
            float weight = 1.0f;
            for (int i = 1; i < realInfluence; i++) {
                weight -= localInfluence[i];
            }
            localInfluence[0] = weight;

            weights.put(localInfluence);
        }

        var vertexBuffers = mesh.vertexBuffers().stream()
            .filter(vb -> vb.info().semantic() != Semantic.JOINTS && vb.info().semantic() != Semantic.WEIGHTS)
            .collect(Collectors.toList());
        vertexBuffers.add(new VertexBuffer<>(joints, VertexBufferInfo.joints(ComponentType.UNSIGNED_SHORT, realInfluence)));
        vertexBuffers.add(new VertexBuffer<>(weights.flip(), VertexBufferInfo.weights(ComponentType.FLOAT, realInfluence)));
        return mesh.withVertexBuffers(vertexBuffers);
    }

    private void fixJointIndices(Md6Model md6, List<Mesh> meshes) {
        var skinnedJoints = md6.header().skinnedJoints();
        var extraJoints = md6.header().extraJoints();
        var skinnedJointsLen8 = (skinnedJoints.length + 7) & ~7;

        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = md6.meshInfos().get(i);
            var offset = meshInfo.lodInfos().getFirst().unknown4();
            var buffer = meshes.get(i)
                .getBuffer(Semantic.JOINTS)
                .map(vb -> (ShortBuffer) vb.buffer())
                .orElseThrow();

            for (var j = 0; j < buffer.limit(); j++) {
                var index0 = Short.toUnsignedInt(buffer.get(j));
                var index1 = index0 + offset;
                short index2;
                if (index1 < skinnedJointsLen8) {
                    index2 = skinnedJoints[index1];
                } else {
                    var index11 = extraJoints[index1 - skinnedJointsLen8];
                    if (index11 < skinnedJointsLen8) {
                        index2 = skinnedJoints[index11];
                    } else {
                        index2 = skinnedJoints[extraJoints[index11 - skinnedJointsLen8]];
                    }
                }
                buffer.put(j, index2);
            }
        }
    }
}
