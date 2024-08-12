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
    public static MapFileStaticInstancesModelExtra read(DataSource source) throws IOException {
        source.expectInt(0);
        source.expectInt(0);
        float unknown008 = source.readFloat();
        float unknown012 = source.readFloat();
        float unknown016 = source.readFloat();
        float unknown020 = source.readFloat();
        float unknown024 = source.readFloat();
        float unknown028 = source.readFloat();
        float unknown032 = source.readFloat();
        int unknown036 = source.readInt();
        int materialGroupOffset = source.readInt();
        int materialGroupCount = source.readInt();
        int group2Offset = source.readInt();
        int group2Count = source.readInt();
        int unknown056 = source.readInt();
        int unknown060 = source.readInt();
        float unknown064 = source.readFloat();
        float unknown068 = source.readFloat();
        float unknown072 = source.readFloat();
        float unknown076 = source.readFloat();
        float unknown080 = source.readFloat();
        float unknown084 = source.readFloat();
        float unknown088 = source.readFloat();
        float unknown092 = source.readFloat();
        float unknown096 = source.readFloat();
        float unknown100 = source.readFloat();
        float unknown104 = source.readFloat();
        float unknown108 = source.readFloat();
        float unknown112 = source.readFloat();
        float unknown116 = source.readFloat();
        int unknown120 = source.readInt();
        int unknown124 = source.readInt();
        source.expectInt(0);
        source.expectInt(0);
        float unknown136 = source.readFloat();
        source.expectInt(-1);
        float unknown144 = source.readFloat();
        short unknown148 = source.readShort();
        source.expectShort((short) 0);

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
