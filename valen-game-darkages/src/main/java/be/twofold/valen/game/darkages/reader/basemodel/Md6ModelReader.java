package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
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
    public Model read(DataSource source, DarkAgesAsset asset) throws IOException {
        var skelName = source.readPString();
        var skeletonKey = DarkAgesAssetID.from(skelName, ResourcesType.Skeleton);
        var skeleton = archive.loadAsset(skeletonKey, Skeleton.class);
        var md6Model = Md6Model.read(source, skeleton.bones().size() + 7 & ~7);
        source.expectEnd();

        var meshes = readMeshes(md6Model, asset.hash());
        if (readMaterials) {
            Materials.apply(archive, meshes, md6Model.meshInfos(), Md6ModelMeshInfo::materialName, Md6ModelMeshInfo::meshName);
        }

        return new Model(meshes, Optional.of(skeleton), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(Md6Model md6Model, long hash) throws IOException {
        var meshes = readStreamedGeometry(md6Model, 0, hash);
        // fixJointIndices(md6Model, meshes);
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(Md6Model md6Model, int lod, long hash) throws IOException {
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

        try (var source = DataSource.fromBuffer(buffer)) {
            List<Mesh> meshes = GeometryReader.readStreamedMesh(source, lodInfos, true);
            meshes = mergeJointsAndWeights(md6Model, meshes);
            fixJointIndices(md6Model, meshes);
            meshes = exportWounds(md6Model, lod, source, meshes);
            return meshes;
        }
    }

    private static List<Mesh> exportWounds(Md6Model md6Model, int lod, DataSource source, List<Mesh> meshes) throws IOException {
        if (md6Model.modelWounds().isEmpty()) {
            return meshes;
        }
        var offsets = md6Model.woundOffsets().get(lod);
        if (offsets.numVertices() == 0) {
            return meshes;
        }
        var newMeshes = new ArrayList<Mesh>(meshes.size());
        int vertexIdOffset = offsets.vertexIDsOffsets();
        int vertexWeightsOffset = offsets.vertexWeightsOffset();

        newMeshes.addAll(meshes);
        for (Md6ModelWound modelWound : md6Model.modelWounds()) {
            for (Md6ModelMeshWound meshWound : modelWound.meshWounds()) {
                var sourceMesh = md6Model.meshInfos().get(meshWound.meshIndex());
                int sourceMeshVertexCount = sourceMesh.lodInfos().getFirst().numVertices();
                var mesh = newMeshes.get(meshWound.meshIndex());
                int woundLodVertexCount = meshWound.offsets()[0];

                source.position(vertexIdOffset);
                var woundVertexIds = source.readShorts(woundLodVertexCount);
                vertexIdOffset += woundLodVertexCount * 2;
                vertexIdOffset = (vertexIdOffset + 7) & ~7;

                source.position(vertexWeightsOffset);
                var woundVertexWeights = source.readBytes(woundLodVertexCount);
                vertexWeightsOffset += woundLodVertexCount;
                vertexWeightsOffset = (vertexWeightsOffset + 3) & ~3;

                var newVertexBuffers = new ArrayList<VertexBuffer<?>>(mesh.vertexBuffers().size() + 1);
                newVertexBuffers.addAll(mesh.vertexBuffers());

                var woundWeights = new byte[sourceMeshVertexCount * 4];
                for (int i = 0; i < woundVertexIds.length; i++) {
                    int vertexId = Short.toUnsignedInt(woundVertexIds[i]);
                    byte weight = woundVertexWeights[i];

                    woundWeights[vertexId * 4/**/] = weight;
                    woundWeights[vertexId * 4 + 1] = weight;
                    woundWeights[vertexId * 4 + 2] = weight;
                    woundWeights[vertexId * 4 + 3] = weight;
                }

                newVertexBuffers.add(new VertexBuffer<>(
                    ByteBuffer.wrap(woundWeights),
                    new VertexBufferInfo<>(Semantic.COLOR, ComponentType.UNSIGNED_BYTE, 4, modelWound.name())
                ));

                var newMesh = new Mesh(mesh.indexBuffer(), newVertexBuffers, mesh.material(), mesh.name(), mesh.blendShapes());
                newMeshes.set(meshWound.meshIndex(), newMesh);

            }
        }
        return newMeshes;
    }

    private ArrayList<Mesh> mergeJointsAndWeights(Md6Model md6, List<Mesh> meshes) {
        var newMeshes = new ArrayList<Mesh>();
        for (int m = 0; m < meshes.size(); m++) {
            Mesh mesh = meshes.get(m);
            var weightBuffers = mesh.getBuffers(Semantic.WEIGHTS);
            if (weightBuffers.isEmpty()) {
                newMeshes.add(mesh);
                continue;
            }
            var influence = weightBuffers.size() > 1 ? weightBuffers.getLast().info().size() + 4 : 4;
            var realInfluence = md6.meshInfos().get(m).lodInfos().getFirst().influence();

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
            newMeshes.add(mesh.withVertexBuffers(vertexBuffers));
        }
        return newMeshes;
    }

    private void fixJointIndices(Md6Model md6, List<Mesh> meshes) {
        int len1 = (md6.header().skinnedJoints().length + 7) & ~7;
        int len2 = (md6.header().extraJoints().length + 7) & ~7;

        var jointRemap = new short[len1 + len2];
        Arrays.fill(jointRemap, (short) -1);
        System.arraycopy(md6.header().skinnedJoints(), 0, jointRemap, 0, md6.header().skinnedJoints().length);
        System.arraycopy(md6.header().extraJoints(), 0, jointRemap, len1, md6.header().extraJoints().length);

        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = md6.meshInfos().get(i);
            var offset = meshInfo.lodInfos().getFirst().unknown4();
            var joints = meshes.get(i)
                .getBuffer(Semantic.JOINTS)
                .orElseThrow();

            // Assume it's a short buffer, because we read it as such
            var buffer = (ShortBuffer) joints.buffer();
            for (var j = 0; j < buffer.limit(); j++) {
                int index = Short.toUnsignedInt(buffer.get(j)) + offset;
                if (jointRemap[index] == -1) {
                    throw new UnsupportedOperationException();
                }
                buffer.put(j, jointRemap[index]);
            }
        }
    }
}
