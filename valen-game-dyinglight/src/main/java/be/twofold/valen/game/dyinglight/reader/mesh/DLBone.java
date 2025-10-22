package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record DLBone(
    Matrix4 localTransform,
    Matrix4 mat1,
    Vector3[] bounds,
    String name,
    FlaggedOffset unkOffset1,
    FlaggedOffset meshOffset,
    FlaggedOffset unkOffset2,
    FlaggedOffset unkOffset3,
    short selfId,
    short parentId,
    byte nodeType,
    byte lodCount,
    short childCount,
    FlaggedOffset unkOffset4
) {
    public static DLBone read(BinaryReader reader) throws IOException {
        var m0 = Vector3.read(reader);
        var posX = reader.readFloat();
        var m1 = Vector3.read(reader);
        var posY = reader.readFloat();
        var m2 = Vector3.read(reader);
        var posZ = reader.readFloat();
        var mat0 = new Matrix4(
            m0.x(), m1.x(), m2.x(), 0,
            m0.y(), m1.y(), m2.y(), 0,
            m0.z(), m1.z(), m2.z(), 0,
            posX, posY, posZ, 1
        );
        m0 = Vector3.read(reader);
        posX = reader.readFloat();
        m1 = Vector3.read(reader);
        posY = reader.readFloat();
        m2 = Vector3.read(reader);
        posZ = reader.readFloat();
        var mat1 = new Matrix4(
            m0.x(), m1.x(), m2.x(), 0,
            m0.y(), m1.y(), m2.y(), 0,
            m0.z(), m1.z(), m2.z(), 0,
            posX, posY, posZ, 1
        );
        var unk = new Vector3[2];
        unk[0] = Vector3.read(reader);
        unk[1] = Vector3.read(reader);
        var nameOffset = FlaggedOffset.read(reader);
        var unkOffset1 = FlaggedOffset.read(reader);
        var meshOffset = FlaggedOffset.read(reader);
        reader.expectLong(0);
        reader.expectLong(0);
        reader.expectLong(0);
        reader.expectLong(0);
        var unkOffset2 = FlaggedOffset.read(reader);
        var unkOffset3 = FlaggedOffset.read(reader);
        var unk2 = reader.readInt();
        var selfId = reader.readShort();
        var parentId = reader.readShort();
        var nodeType = reader.readByte();
        var lodCount = reader.readByte();
        var childCount = reader.readShort();
        reader.expectInt(0);
        var unkOffset4 = FlaggedOffset.read(reader);
        reader.expectInt(0);
        reader.expectInt(0);
        String name = "<NO_NAME>";
        if (nameOffset.isValid()) {
            var currentPos = reader.position();
            reader.position(nameOffset.offset());
            name = reader.readCString();
            reader.position(currentPos);
        }
        return new DLBone(mat0, mat1, unk, name, unkOffset1, meshOffset, unkOffset2, unkOffset3, selfId, parentId, nodeType, lodCount, childCount, unkOffset4);
    }
}
