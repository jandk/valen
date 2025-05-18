package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.geometry.*;
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class Md6ModelReader implements AssetReader<Model, EternalAsset> {
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
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.BaseModel;
    }

    @Override
    public Model read(DataSource source, EternalAsset resource) throws IOException {
        var model = Md6Model.read(source);
        var meshes = new ArrayList<>(readMeshes(model, resource.hash()));
        var skeletonKey = EternalAssetID.from(model.header().md6SkelName(), ResourceType.Skeleton);
        var skeleton = archive.loadAsset(skeletonKey, Skeleton.class);

        if (readMaterials) {
            Materials.apply(archive, meshes, model.meshInfos(), Md6ModelInfo::materialName, Md6ModelInfo::meshName);
        }
        return new Model(meshes, Optional.of(skeleton), Optional.of(resource.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(Md6Model md6, long hash) throws IOException {
        var meshes = readStreamedGeometry(md6, 0, hash);
        fixJointIndices(md6, meshes);
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
                .getBuffer(Semantic.JOINTS)
                .orElseThrow();

            // Just assume it's a byte buffer, because we read it as such
            var array = ((ByteBuffer) joints.buffer()).array();
            for (var j = 0; j < array.length; j++) {
                array[j] = lookup[Byte.toUnsignedInt(array[j]) + meshInfo.unknown2()];
            }
        }
    }
}
