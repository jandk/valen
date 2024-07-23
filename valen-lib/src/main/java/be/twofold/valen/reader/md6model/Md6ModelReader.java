package be.twofold.valen.reader.md6model;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.geometry.*;
import be.twofold.valen.resource.*;
import dagger.*;
import jakarta.inject.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class Md6ModelReader implements ResourceReader<Model> {
    private final Lazy<FileManager> fileManager;
    private final boolean readMaterials;

    @Inject
    Md6ModelReader(Lazy<FileManager> fileManager) {
        this(fileManager, true);
    }

    Md6ModelReader(Lazy<FileManager> fileManager, boolean readMaterials) {
        this.fileManager = fileManager;
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.BaseModel;
    }

    @Override
    public Model read(DataSource source, Resource resource) throws IOException {
        Md6Model model = read(source, true, resource.hash());
        Skeleton skeleton = fileManager.get().readResource(model.header().md6SkelName(), FileType.Skeleton);

        if (readMaterials) {
            var materials = new LinkedHashMap<String, Material>();
            var materialIndices = new HashMap<String, Integer>();

            var meshes = new ArrayList<Mesh>();
            for (int i = 0; i < model.meshes().size(); i++) {
                var meshInfo = model.meshInfos().get(i);
                var materialName = meshInfo.materialName();
                var materialFile = "generated/decls/material2/" + materialName + ".decl";
                var materialIndex = materialIndices.computeIfAbsent(materialName, k -> materials.size());
                if (!materials.containsKey(materialName)) {
                    Material material = fileManager.get().readResource(materialFile, FileType.Material);
                    materials.put(materialName, material);
                }
                meshes.add(model.meshes().get(i).withMaterialIndex(materialIndex));
            }
            model = model
                .withMeshes(meshes)
                .withMaterials(List.copyOf(materials.values()));
        }
        return new Model(model.meshes(), model.materials(), skeleton);
    }

    public Md6Model read(DataSource source, boolean readStreams, long hash) throws IOException {
        var md6 = Md6Model.read(source);

        List<Mesh> meshes;
        if (readStreams) {
            meshes = readStreamedGeometry(md6, 0, hash);
            fixJointIndices(md6, meshes);
        } else {
            meshes = List.of();
        }
        return md6.withMeshes(meshes);
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
        var source = new ByteArrayDataSource(fileManager.get().readStream(identity, uncompressedSize));
        return GeometryReader.readStreamedMesh(source, lodInfos, layouts, true);
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
