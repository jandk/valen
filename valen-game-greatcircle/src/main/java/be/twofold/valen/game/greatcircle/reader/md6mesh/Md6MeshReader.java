package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.geometry.*;
import be.twofold.valen.game.greatcircle.resource.*;
import be.twofold.valen.game.idtech.geometry.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class Md6MeshReader implements AssetReader<Model, GreatCircleAsset> {
    private static final Logger log = LoggerFactory.getLogger(Md6MeshReader.class);
    private final GreatCircleArchive archive;
    private final boolean readMaterials;

    public Md6MeshReader(GreatCircleArchive archive) {
        this(archive, true);
    }

    Md6MeshReader(GreatCircleArchive archive, boolean readMaterials) {
        this.archive = archive;
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(GreatCircleAsset resource) {
        return resource.id().type() == ResourceType.basemodel;
    }

    @Override
    public Model read(BinarySource source, GreatCircleAsset asset) throws IOException {
        var model = Md6Mesh.read(source);
        if (model.header().skeletonName().equals("models/characters/abgal/abgal_wear_base.md6skl")) {
            System.out.println("Fount it!");
        }
        var skeletonKey = GreatCircleAssetID.from(model.header().skeletonName(), ResourceType.skeleton);
        var skeleton = archive.loadAsset(skeletonKey, Skeleton.class);
        var meshes = new ArrayList<>(readMeshes(model, asset.hash()));

        if (readMaterials) {
            Materials.apply(archive, meshes, model.meshInfos(), Md6MeshInfo::materialName, Md6MeshInfo::meshName);
        }
        return new Model(meshes, Optional.ofNullable(skeleton), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(Md6Mesh md6, long hash) throws IOException {
        var meshes = readStreamedGeometry(md6, 0, hash);
        fixJointIndices(md6, meshes);
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(Md6Mesh md6, int lod, long hash) throws IOException {
        var uncompressedSize = md6.layouts().get(lod).uncompressedSize();
        if (uncompressedSize == 0) {
            return List.of();
        }

        var lodInfos = md6.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = md6.layouts().get(lod).memoryLayouts();

        var identity = (hash << 4) | lod;
        var bytes = archive.readStream(identity, uncompressedSize);
        try (var source = BinarySource.wrap(bytes)) {
            var meshes = GeometryReader.readStreamedMesh(source, lodInfos, layouts, true);
            var allBlendShapes = BlendShapeReader.readBlendShapes(source, lodInfos, layouts);

            var newMeshes = new ArrayList<Mesh>(meshes.size());
            for (int i = 0; i < meshes.size(); i++) {
                var mesh = meshes.get(i);
                var blendShapes = allBlendShapes.get(i);
                if (blendShapes != null && !blendShapes.isEmpty()) {
                    mesh = mesh.withBlendShapes(blendShapes);
                }
                newMeshes.add(mesh);
            }
            return List.copyOf(newMeshes);
        }
    }

    private void fixJointIndices(Md6Mesh md6, List<Mesh> meshes) {
        var jointRemap = md6.boneInfo().jointRemap();

        // This lookup table is in reverse... Nice
        var lookup = new byte[jointRemap.length()];
        for (var i = 0; i < jointRemap.length(); i++) {
            lookup[Short.toUnsignedInt(jointRemap.get(i))] = (byte) i;
        }

        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = md6.meshInfos().get(i);

            // Just assume it's a byte buffer, because we read it as such
            var joints = meshes.get(i).joints().map(Shorts.Mutable.class::cast).orElseThrow();
            for (var j = 0; j < joints.length(); j++) {
                joints.set(j, lookup[joints.getUnsigned(j) + meshInfo.unknown2()]);
            }
        }
    }
}
