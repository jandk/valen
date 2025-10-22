package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.*;

public record DLMeshLod(
    int unk0,
    int unk01,
    short unk1,
    short unk2,
    short vertexType,
    long vertexDataOffsetInVertexSection,
    int vertexCount,
    long indexDataOffsetInIndexSection,
    DLSubMeshes subMeshes
) {

    public static DLMeshLod read(BinaryReader reader) throws IOException {
        var unk0 = reader.readInt();
        var unk01 = reader.readInt();
        var materialIndicesOffset = FlaggedOffset.read(reader);
        var subMeshCount = reader.readShort();
        var unk1 = reader.readShort();

        var unk2 = reader.readShort();
        var vertexType = reader.readShort();
        var meshOffset = FlaggedOffset.read(reader);
        var boneRemapOffset = FlaggedOffset.read(reader);
        var vertexDataOffsetInVertexSection = reader.readInt();
        var vertexCount = reader.readInt();
        var indexDataOffsetInIndexSection = reader.readInt();

        short[] materialIds = null;
        int[] indexCounts = null;
        List<Shorts> boneMaps = null;

        if (materialIndicesOffset.isValid()) {
            reader.position(materialIndicesOffset.offset());
            materialIds = reader.readShorts(subMeshCount);
        }
        if (meshOffset.isValid()) {
            reader.position(meshOffset.offset());
            indexCounts = reader.readInts(subMeshCount);
        }
        if (boneRemapOffset.isValid()) {
            boneMaps = new ArrayList<>(subMeshCount);
            reader.position(boneRemapOffset.offset());
            for (int i = 0; i < subMeshCount; i++) {
                boneMaps.add(DLBoneMap.read(reader));
            }
        }

        var subMeshes = new DLSubMeshes(
            subMeshCount,
            Optional.ofNullable(indexCounts).map(Ints::wrap),
            Optional.ofNullable(boneMaps),
            Optional.ofNullable(materialIds).map(Shorts::wrap)
        );
        return new DLMeshLod(
            unk0,
            unk01,
            unk1,
            unk2,
            vertexType,
            vertexDataOffsetInVertexSection,
            vertexCount,
            indexDataOffsetInIndexSection,
            subMeshes
        );
    }

    public int vertexSize() {
        return switch (vertexType) {
            case 0 -> 16;
            case 1, 2 -> 20;
            case 3, 4, 5, 7 -> 32;
            case 6 -> 40;
            case 8 -> 80;
            default -> throw new IllegalStateException("Unexpected vertex type: " + vertexType);
        };
    }
}
