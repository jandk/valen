package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.*;
import be.twofold.valen.game.eternal.reader.geometry.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class Md6ModelReader implements ResourceReader<Model> {
    private final EternalArchive archive;
    private final boolean readMaterials;

    public Md6ModelReader(EternalArchive archive) {
        this(archive, true);
    }

    Md6ModelReader(EternalArchive archive, boolean readMaterials) {
        this.archive = archive;
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.BaseModel;
    }

    @Override
    public Model read(DataSource source, Asset asset) throws IOException {
        var model = Md6Model.read(source);
        var meshes = new ArrayList<>(readMeshes(model, (Long) asset.properties().get("hash")));
        var skeletonKey = ResourceKey.from(model.header().md6SkelName(), ResourceType.Skeleton);
        var skeleton = (Skeleton) archive.loadAsset(skeletonKey);

        if (readMaterials) {
            var materials = new HashMap<String, Material>();
            for (int i = 0; i < meshes.size(); i++) {
                var meshInfo = model.meshInfos().get(i);
                var materialName = meshInfo.materialName();
                var materialFile = "generated/decls/material2/" + materialName + ".decl";
                if (!materials.containsKey(materialName)) {
                    var assetId = ResourceKey.from(materialFile, ResourceType.RsStreamFile);
                    var material = (Material) archive.loadAsset(assetId);
                    materials.put(materialName, material);
                }
                meshes.set(i, meshes.get(i)
                    .withMaterial(materials.get(materialName)));
            }
        }
        return new Model(meshes, skeleton);
    }

    private List<Mesh> readMeshes(Md6Model md6, long hash) throws IOException {
        var meshes = readStreamedGeometry(md6, 0, hash);
        fixJointIndices(md6, meshes);

        // Add names to all meshes
        for (int i = 0; i < meshes.size(); i++) {
            meshes.set(i, meshes.get(i).withName(md6.meshInfos().get(i).meshName()));
        }
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(Md6Model md6, int lod, long hash) throws IOException {
        var uncompressedSize = md6.layouts().get(lod).uncompressedSize();
        if (uncompressedSize == 0) {
            return List.of();
        }

        var lodInfos = md6.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = md6.layouts().get(lod).memoryLayouts();

        var identity = (hash << 4) | lod;
        var buffer = archive.readStream(identity, uncompressedSize);
        try (var source = DataSource.fromBuffer(buffer)) {
            return GeometryReader.readStreamedMesh(source, lodInfos, layouts, true);
        }
    }

    private void fixJointIndices(Md6Model md6, List<Mesh> meshes) {
        var jointRemap = md6.boneInfo().jointRemap();

        // This lookup table is in reverse... Nice
        var lookup = new byte[jointRemap.length];
        for (var i = 0; i < jointRemap.length; i++) {
            lookup[Byte.toUnsignedInt(jointRemap[i])] = (byte) i;
        }

        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = md6.meshInfos().get(i);
            var joints = meshes.get(i)
                .getBuffer(Semantic.Joints0)
                .orElseThrow();

            // Just assume it's a byte buffer, because we read it as such
            var array = ((ByteBuffer) joints.buffer()).array();
            for (var j = 0; j < array.length; j++) {
                array[j] = lookup[Byte.toUnsignedInt(array[j]) + meshInfo.unknown2()];
            }
        }
    }
}
