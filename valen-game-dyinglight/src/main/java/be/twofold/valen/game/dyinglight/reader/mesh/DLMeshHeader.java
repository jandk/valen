package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.io.*;

import java.io.*;

public record DLMeshHeader(
    String name,
    FlaggedOffset bonesOffset,
    FlaggedOffset surfParams,
    FlaggedOffset materialsOffset,
    FlaggedOffset morphTargetNames,
    FlaggedOffset collisionTree,
    int unk30,
    int unk34,
    FlaggedOffset occlusionHull,
    String scriptName,
    FlaggedOffset meshData,
    FlaggedOffset twistBone,
    int boneCount,
    int rootCount,
    int surfParamCount,
    int morphCount,
    int shadowCasterMaterialIndex,
    int unk68
) {

    public static DLMeshHeader read(BinaryReader reader) throws IOException {
        var name = OffsetString.read(reader, false);
        var bonesOffset = FlaggedOffset.read(reader);
        var surfParams = FlaggedOffset.read(reader);
        var materialsOffset = FlaggedOffset.read(reader);
        var morphTargetNames = FlaggedOffset.read(reader);
        var collisionTree = FlaggedOffset.read(reader);
        var unk30 = reader.readInt();
        var unk34 = reader.readInt();
        var occlusionHull = FlaggedOffset.read(reader);
        var scriptName = OffsetString.read(reader, false);
        var meshData = FlaggedOffset.read(reader);
        var twistBone = FlaggedOffset.read(reader);
        var boneCount = reader.readInt();
        var rootCount = reader.readInt();
        var surfParamCount = reader.readInt();
        var morphCount = reader.readInt();
        var shadowCasterMaterialIndex = reader.readInt();
        var unk68 = reader.readInt();
        return new DLMeshHeader(
            name,
            bonesOffset,
            surfParams,
            materialsOffset,
            morphTargetNames,
            collisionTree,
            unk30,
            unk34,
            occlusionHull,
            scriptName,
            meshData,
            twistBone,
            boneCount,
            rootCount,
            surfParamCount,
            morphCount,
            shadowCasterMaterialIndex,
            unk68
        );
    }
}
