package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.darkages.reader.geometry.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.util.*;

public final class Md6ModelReader implements AssetReader<Model, DarkAgesAsset> {
    private final DarkAgesArchive archive;
    private final boolean readMaterials;

    public Md6ModelReader(DarkAgesArchive archive, boolean readMaterials) {
        this.archive = archive;
        this.readMaterials = false;
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
        var bytes = archive.readStream(identity, uncompressedSize);

        try (var source = BinaryReader.fromBytes(bytes)) {
            List<Mesh> meshes = GeometryReader.readStreamedMesh(source, lodInfos, true);
            meshes = mergeJointsAndWeights(md6Model, meshes);
            fixJointIndices(md6Model, meshes);
            return meshes;
        }
    }

    private ArrayList<Mesh> mergeJointsAndWeights(Md6Model md6, List<Mesh> meshes) {
        var newMeshes = new ArrayList<Mesh>();
        for (int m = 0; m < meshes.size(); m++) {
            int realInfluence = md6.meshInfos().get(m).lodInfos().getFirst().influence();
            newMeshes.add(mergeJointsAndWeights(meshes.get(m), realInfluence));
        }
        return newMeshes;
    }

    private static Mesh mergeJointsAndWeights(Mesh mesh, int realInfluence) {
        if (realInfluence == 1) {
            return mesh.withMaxInfluence(1);
        }

        var first = mesh.custom().get("W");
        var influence = (first == null ? 0 : first.length()) + 4;

        var joints = compactJoints(mesh, influence, realInfluence);
        var weights = mergeWeights(
            mesh, influence, realInfluence,
            first != null ? (Floats) first.array() : Floats.empty(),
            mesh.weights().orElseThrow()
        );

        return mesh
            .withJointsAndWeights(joints, weights)
            .withMaxInfluence(realInfluence);
    }

    private static Floats mergeWeights(Mesh mesh, int influence, int realInfluence, Floats buffer1, Floats buffer2) {
        var weights = MutableFloats.allocate(mesh.vertexCount() * realInfluence);

        var localInfluence = new float[realInfluence];
        for (int c = 0, i1 = 0, i2 = 0, o = 0; c < mesh.vertexCount(); c++) {
            for (int i = 1; i < influence - 3; i++) {
                localInfluence[i] = buffer1.getFloat(i2++);
            }
            i1++; // skip calculated weight
            for (int i = influence - 3; i < realInfluence; i++) {
                localInfluence[i] = buffer2.getFloat(i1++);
            }
            float weight = 1.0f;
            for (int i = 1; i < realInfluence; i++) {
                weight -= localInfluence[i];
            }
            localInfluence[0] = weight;

            Floats.wrap(localInfluence).copyTo(weights, o);
            o += localInfluence.length;
        }
        return weights;
    }

    private static MutableShorts compactJoints(Mesh mesh, int influence, int realInfluence) {
        var joints = (MutableShorts) mesh.joints().orElseThrow();
        if (influence == realInfluence) {
            return joints;
        }

        for (int i = 0, o = 0, lim = joints.size(); i < lim; i += influence, o += realInfluence) {
            for (int j = 0; j < realInfluence; j++) {
                joints.setShort(o + j, joints.getShort(i + j));
            }
        }
        return joints.slice(0, mesh.vertexCount() * realInfluence);
    }

    private void fixJointIndices(Md6Model md6, List<Mesh> meshes) {
        var skinnedJoints = md6.header().skinnedJoints();
        var extraJoints = md6.header().extraJoints();
        var skinnedJointsLen8 = (skinnedJoints.length + 7) & ~7;

        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = md6.meshInfos().get(i);
            var offset = meshInfo.lodInfos().getFirst().unknown4();
            var shorts = meshes.get(i).joints().map(MutableShorts.class::cast).orElseThrow();

            for (var j = 0; j < shorts.size(); j++) {
                var index0 = Short.toUnsignedInt(shorts.getShort(j));
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
                shorts.setShort(j, index2);
            }
        }
    }
}
