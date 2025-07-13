package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MapFileStaticInstancesModelExtra(
    float unknown008,
    float unknown012,
    float unknown016,
    float unknown020,
    float unknown024,
    float unknown028,
    float unknown032,
    int unknown036,
    int materialGroupOffset,
    int materialGroupCount,
    int group2Offset,
    int group2Count,
    int unknown056,
    int unknown060,
    float unknown064,
    float unknown068,
    float unknown072,
    float unknown076,
    float unknown080,
    float unknown084,
    float unknown088,
    float unknown092,
    float unknown096,
    float unknown100,
    float unknown104,
    float unknown108,
    float unknown112,
    float unknown116,
    int unknown120,
    int unknown124,
    float unknown136,
    float unknown144,
    short unknown148
) {
    public static MapFileStaticInstancesModelExtra read(BinaryReader reader) throws IOException {
        reader.expectInt(0);
        reader.expectInt(0);
        float unknown008 = reader.readFloat();
        float unknown012 = reader.readFloat();
        float unknown016 = reader.readFloat();
        float unknown020 = reader.readFloat();
        float unknown024 = reader.readFloat();
        float unknown028 = reader.readFloat();
        float unknown032 = reader.readFloat();
        int unknown036 = reader.readInt();
        int materialGroupOffset = reader.readInt();
        int materialGroupCount = reader.readInt();
        int group2Offset = reader.readInt();
        int group2Count = reader.readInt();
        int unknown056 = reader.readInt();
        int unknown060 = reader.readInt();
        float unknown064 = reader.readFloat();
        float unknown068 = reader.readFloat();
        float unknown072 = reader.readFloat();
        float unknown076 = reader.readFloat();
        float unknown080 = reader.readFloat();
        float unknown084 = reader.readFloat();
        float unknown088 = reader.readFloat();
        float unknown092 = reader.readFloat();
        float unknown096 = reader.readFloat();
        float unknown100 = reader.readFloat();
        float unknown104 = reader.readFloat();
        float unknown108 = reader.readFloat();
        float unknown112 = reader.readFloat();
        float unknown116 = reader.readFloat();
        int unknown120 = reader.readInt();
        int unknown124 = reader.readInt();
        reader.expectInt(0);
        reader.expectInt(0);
        float unknown136 = reader.readFloat();
        reader.expectInt(-1);
        float unknown144 = reader.readFloat();
        short unknown148 = reader.readShort();
        reader.expectShort((short) 0);

        return new MapFileStaticInstancesModelExtra(
            unknown008,
            unknown012,
            unknown016,
            unknown020,
            unknown024,
            unknown028,
            unknown032,
            unknown036,
            materialGroupOffset,
            materialGroupCount,
            group2Offset,
            group2Count,
            unknown056,
            unknown060,
            unknown064,
            unknown068,
            unknown072,
            unknown076,
            unknown080,
            unknown084,
            unknown088,
            unknown092,
            unknown096,
            unknown100,
            unknown104,
            unknown108,
            unknown112,
            unknown116,
            unknown120,
            unknown124,
            unknown136,
            unknown144,
            unknown148
        );
    }
}
