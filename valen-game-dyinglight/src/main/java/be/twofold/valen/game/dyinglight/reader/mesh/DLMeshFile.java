package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.util.*;

public record DLMeshFile(
    DLMeshHeader header,
    List<DLBone> bones,
    List<DLMesh> meshes,
    DLMaterialList materials
) {
    public DLMeshFile {
        bones = List.copyOf(bones);
        meshes = List.copyOf(meshes);
    }

    public static DLMeshFile read(BinaryReader reader) throws IOException {
        var header = DLMeshHeader.read(reader);

        var bones = new ArrayList<DLBone>(header.boneCount());
        if (header.boneCount() > 0) {
            Check.state(header.bonesOffset().isValid());
            reader.position(header.bonesOffset().offset());
            for (int i = 0; i < header.boneCount(); i++) {
                bones.add(DLBone.read(reader));
            }
        }

        var meshes = new ArrayList<DLMesh>();
        for (DLBone bone : bones) {
            if (!bone.meshOffset().isValid()) continue;

            var lods = new ArrayList<DLMeshLod>(bone.lodCount());
            for (int i = 0; i < bone.lodCount(); i++) {
                reader.position(bone.meshOffset().offset() + 64L * i);
                lods.add(DLMeshLod.read(reader));
            }
            meshes.add(new DLMesh(lods, bone));
        }
        Check.state(header.materialsOffset().isValid(), "Expected valid materials offset");
        reader.position(header.materialsOffset().offset());
        var materials = DLMaterialList.read(reader);
        return new DLMeshFile(header, bones, meshes, materials);
    }
}
