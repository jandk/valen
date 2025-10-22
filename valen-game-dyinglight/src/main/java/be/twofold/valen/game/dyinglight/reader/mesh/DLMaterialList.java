package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record DLMaterialList(
    short unk08,
    int unk0C,
    List<DLMaterial> materials
) {
    public DLMaterialList {
        materials = List.copyOf(materials);
    }

    public static DLMaterialList read(BinaryReader reader) throws IOException {
        var offset = FlaggedOffset.read(reader);
        var unk08 = reader.readShort();
        var materialCount = reader.readShort();
        var unk0C = reader.readInt();
        var materials = new ArrayList<DLMaterial>(materialCount);
        if (!offset.isValid()) {
            return new DLMaterialList(unk08, unk0C, materials);
        }
        var currentPos = reader.position();
        reader.position(offset.offset());
        for (int i = 0; i < materialCount; i++) {
            materials.add(DLMaterial.read(reader));
        }
        reader.position(currentPos);
        return new DLMaterialList(unk08, unk0C, materials);
    }

    public DLMaterial getMaterial(short materialId) {
        return materials.get(materialId);
    }

}
