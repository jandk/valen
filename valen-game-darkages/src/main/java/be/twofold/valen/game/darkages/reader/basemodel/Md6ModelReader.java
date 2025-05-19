package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.function.*;
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
            // Materials.apply(archive, meshes, md6Model.meshInfos(), Md6ModelMeshInfo::materialName, Md6ModelMeshInfo::meshName);
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

        // TODO: Clean up hash method
        var key = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        key.putLong(0, hash);
        key.putInt(8, 4 - lod);
        var identity = Hash.hash(key);

        var buffer = archive.readStream(identity, uncompressedSize);

        try (var source = DataSource.fromBuffer(buffer)) {
            List<Mesh> meshes = GeometryReader.readStreamedMesh(source, lodInfos, true);
            meshes = mergeJointsAndWeights(meshes);
            fixJointIndices(md6Model, meshes);
            return meshes;
        }
    }

    private ArrayList<Mesh> mergeJointsAndWeights(List<Mesh> meshes) {
        var newMeshes = new ArrayList<Mesh>();
        for (Mesh mesh : meshes) {
            var weightBuffers = mesh.getBuffers(Semantic.WEIGHTS);
            if (weightBuffers.size() <= 1) {
                newMeshes.add(mesh);
                continue;
            }
            System.out.println(weightBuffers);

            assert weightBuffers.size() == 2;

            int influence = weightBuffers.getLast().info().size() + 4;
            int count = weightBuffers.getFirst().count();

            var buffer1 = (FloatBuffer) weightBuffers.getFirst().buffer();
            var buffer2 = (FloatBuffer) weightBuffers.getLast().buffer();
            var weights = FloatBuffer.allocate(influence * count);

            var localInfluence = new float[influence];
            for (int c = 0; c < count; c++) {
                for (int i = 1; i < influence - 3; i++) {
                    localInfluence[i] = buffer2.get();
                }
                for (int i = influence - 3; i < influence; i++) {
                    localInfluence[i] = buffer1.get();
                }
                float weight = 1.0f;
                for (int i = 1; i < influence; i++) {
                    weight -= localInfluence[i];
                }
                localInfluence[0] = weight;

                weights.put(localInfluence);
            }

            var vertexBuffers = mesh.vertexBuffers().stream()
                .filter(vb -> vb.info().semantic() != Semantic.WEIGHTS)
                .collect(Collectors.toList());
            vertexBuffers.add(new VertexBuffer<>(weights.flip(), VertexBufferInfo.weights(ComponentType.FLOAT, influence)));
            newMeshes.add(mesh.withVertexBuffers(vertexBuffers));
        }
        return newMeshes;
    }

    private void fixJointIndices(Md6Model md6, List<Mesh> meshes) {
        var jointRemap = md6.header().skinnedJoints();

        System.out.println(md6.meshInfos().stream().map(mi -> mi.unknown1()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
        System.out.println(md6.meshInfos().stream().map(mi -> mi.unknown2()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
        System.out.println(md6.meshInfos().stream().map(mi -> mi.unknown3()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
        System.out.println(md6.meshInfos().stream().map(mi -> mi.lodInfos().getFirst().unknown1()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = md6.meshInfos().get(i);
            var joints = meshes.get(i)
                .getBuffer(Semantic.JOINTS)
                .orElseThrow();

            // Assume it's a short buffer, because we read it as such
            var array = ((ShortBuffer) joints.buffer()).array();
            System.out.println("Max: " + IntStream.range(0, array.length).mapToLong(l -> array[l]).max() + meshInfo.unknown3());
            for (var j = 0; j < array.length; j++) {
                short i1 = jointRemap[array[j]];
                if (i1 == 0) {
                    System.out.println();
                }
                array[j] = i1;
            }
        }
    }
}
