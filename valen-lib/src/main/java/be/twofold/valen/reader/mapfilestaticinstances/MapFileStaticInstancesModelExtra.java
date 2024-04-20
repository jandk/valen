package be.twofold.valen.reader.mapfilestaticinstances;

import be.twofold.valen.core.util.*;

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
    public static MapFileStaticInstancesModelExtra read(BetterBuffer buffer) {
        buffer.expectInt(0);
        buffer.expectInt(0);
        float unknown008 = buffer.getFloat();
        float unknown012 = buffer.getFloat();
        float unknown016 = buffer.getFloat();
        float unknown020 = buffer.getFloat();
        float unknown024 = buffer.getFloat();
        float unknown028 = buffer.getFloat();
        float unknown032 = buffer.getFloat();
        int unknown036 = buffer.getInt();
        int materialGroupOffset = buffer.getInt();
        int materialGroupCount = buffer.getInt();
        int group2Offset = buffer.getInt();
        int group2Count = buffer.getInt();
        int unknown056 = buffer.getInt();
        int unknown060 = buffer.getInt();
        float unknown064 = buffer.getFloat();
        float unknown068 = buffer.getFloat();
        float unknown072 = buffer.getFloat();
        float unknown076 = buffer.getFloat();
        float unknown080 = buffer.getFloat();
        float unknown084 = buffer.getFloat();
        float unknown088 = buffer.getFloat();
        float unknown092 = buffer.getFloat();
        float unknown096 = buffer.getFloat();
        float unknown100 = buffer.getFloat();
        float unknown104 = buffer.getFloat();
        float unknown108 = buffer.getFloat();
        float unknown112 = buffer.getFloat();
        float unknown116 = buffer.getFloat();
        int unknown120 = buffer.getInt();
        int unknown124 = buffer.getInt();
        buffer.expectInt(0);
        buffer.expectInt(0);
        float unknown136 = buffer.getFloat();
        buffer.expectInt(-1);
        float unknown144 = buffer.getFloat();
        short unknown148 = buffer.getShort();
        buffer.expectShort(0);

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
