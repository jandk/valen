package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.darkages.reader.geometry.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class Md6ModelReader implements AssetReader.Binary<Model, DarkAgesAsset> {
    private final int PACKED_WEIGHTS = 4;

    private final boolean readMaterials;

    public Md6ModelReader(boolean readMaterials) {
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.BaseModel;
    }

    @Override
    public Model read(BinarySource source, DarkAgesAsset asset, LoadingContext context) throws IOException {
        var skelName = source.readString(StringFormat.INT_LENGTH);
        var skeletonKey = DarkAgesAssetID.from(skelName, ResourcesType.Skeleton);
        var skeleton = context.load(skeletonKey, Skeleton.class);
        var md6Model = Md6Model.read(source, skeleton.bones().size() + 7 & ~7);
        source.expectEnd();

        var meshes = readMeshes(md6Model, 0, asset.hash(), context);
        if (readMaterials) {
            meshes = Materials.apply(context, meshes, md6Model.meshInfos(), Md6ModelMeshInfo::materialName, Md6ModelMeshInfo::meshName);
        }

        return new Model(meshes, Optional.of(skeleton), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(Md6Model md6Model, int lod, long hash, LoadingContext context) throws IOException {
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
        var bytes = context.open(new DarkAgesStreamLocation(identity, uncompressedSize));

        try (var source = BinarySource.wrap(bytes)) {
            var meshes = GeometryReader.readStreamedMesh(source, lodInfos, true);
            meshes = meshes.stream()
                .map(this::mergeJointsAndWeights)
                .map(this::trimUnusedInfluences)
                .toList();
            fixJointIndices(md6Model, meshes);
            return meshes;
        }
    }

    private Mesh mergeJointsAndWeights(Mesh mesh) {
        var joints = mesh.joints().orElse(null);
        if (joints == null || mesh.vertexCount() == 0) {
            return mesh;
        }

        // A single influence is always a full weight, with nothing to merge
        var influence = joints.length() / mesh.vertexCount();
        if (influence <= 1) {
            return mesh;
        }

        var extra = mesh.custom().get("W");
        var weights = mergeWeights(
            mesh, influence,
            extra != null ? (Floats) extra.array() : Floats.empty(),
            mesh.weights().orElse(Floats.empty())
        );

        return mesh.toBuilder()
            .weights(weights)
            .build();
    }

    private Floats mergeWeights(Mesh mesh, int influence, Floats extra, Floats packed) {
        var weights = Floats.Mutable.allocate(mesh.vertexCount() * influence);

        var packedPerVertex = GeometryReader.packedWeightCount(influence);
        var extraPerVertex = GeometryReader.extraWeightCount(influence);
        var localInfluence = new float[influence];

        for (var c = 0; c < mesh.vertexCount(); c++) {
            for (var i = 0; i < extraPerVertex; i++) {
                localInfluence[1 + i] = extra.get(c * extraPerVertex + i);
            }
            for (var i = 0; i < packedPerVertex; i++) {
                localInfluence[1 + extraPerVertex + i] = packed.get(c * PACKED_WEIGHTS + 1 + i);
            }

            var weight = 1.0f;
            for (var i = 1; i < influence; i++) {
                weight -= localInfluence[i];
            }
            localInfluence[0] = weight;

            weights.slice(c * influence).copyFrom(localInfluence);
        }
        return weights;
    }

    private Mesh trimUnusedInfluences(Mesh mesh) {
        var joints = mesh.joints().orElse(null);
        var weights = mesh.weights().orElse(null);
        if (joints == null || weights == null || mesh.vertexCount() == 0) {
            return mesh;
        }

        var influence = weights.length() / mesh.vertexCount();
        var used = 1;
        for (var c = 0; c < mesh.vertexCount() && used < influence; c++) {
            for (var i = influence - 1; i >= used; i--) {
                if (weights.get(c * influence + i) != 0.0f) {
                    used = i + 1;
                    break;
                }
            }
        }
        if (used == influence) {
            return mesh;
        }

        var newJoints = Shorts.Mutable.allocate(mesh.vertexCount() * used);
        var newWeights = Floats.Mutable.allocate(mesh.vertexCount() * used);
        for (var c = 0; c < mesh.vertexCount(); c++) {
            for (var i = 0; i < used; i++) {
                newJoints.set(c * used + i, joints.get(c * influence + i));
                newWeights.set(c * used + i, weights.get(c * influence + i));
            }
        }

        return mesh.toBuilder()
            .joints(newJoints)
            .weights(newWeights)
            .build();
    }

    private void fixJointIndices(Md6Model md6, List<Mesh> meshes) {
        var joints = resolveJoints(md6.header());

        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = md6.meshInfos().get(i);
            var offset = meshInfo.lodInfos().getFirst().unknown4();
            var shorts = meshes.get(i).joints().map(Shorts.Mutable.class::cast).orElseThrow();

            for (var j = 0; j < shorts.length(); j++) {
                shorts.set(j, joints[shorts.getUnsigned(j) + offset]);
            }
        }
    }

    /**
     * Build a chain of joints from the skinned joints and extra joints.
     */
    private short[] resolveJoints(Md6ModelHeader header) {
        var skinnedJoints = header.skinnedJoints();
        var extraJoints = header.extraJoints();
        var skinnedJointsLen8 = skinnedJoints.length() + 7 & ~7;

        var resolved = new short[skinnedJointsLen8 + extraJoints.length()];
        for (var i = 0; i < skinnedJoints.length(); i++) {
            resolved[i] = skinnedJoints.get(i);
        }
        for (var i = 0; i < extraJoints.length(); i++) {
            resolved[skinnedJointsLen8 + i] = resolved[extraJoints.getUnsigned(i)];
        }
        return resolved;
    }
}
