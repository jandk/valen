package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.util.*;

public record Md6ModelHeader(
    Vector3 minBoundsExpansion,
    Vector3 maxBoundsExpansion,
    boolean remapForSkinning,
    String unknown,
    short[] skinnedJoints,
    short[] extraJoints,
    Bounds defaultBounds,
    int numLods,
    float[] maxLodDeviations,
    List<String> blendShapeNames,
    Vector3 unknown1,
    Vector3 unknown2,
    Vector3 unknown3,
    float[] jointBoundRadius
) {
    public static Md6ModelHeader read(DataSource source, int numJoints8) throws IOException {
        var minBoundsExpansion = Vector3.read(source);
        var maxBoundsExpansion = Vector3.read(source);
        var remapForSkinning = source.readBoolByte();
        var unknown = source.readPString();
        var skinnedJoints = source.readShorts(Short.toUnsignedInt(source.readShort()));
        var extraJoints = source.readShorts(Short.toUnsignedInt(source.readShort()));
        var defaultBounds = Bounds.read(source);
        var numLods = source.readInt();
        var maxLodDeviations = source.readFloats(5);
        var blendShapeNames = source.readObjects(source.readInt(), DataSource::readPString);
        var unknown1 = Vector3.read(source);
        var unknown2 = Vector3.read(source);
        var unknown3 = Vector3.read(source);
        var jointBoundRadius = source.readFloats(numJoints8);

        return new Md6ModelHeader(
            minBoundsExpansion,
            maxBoundsExpansion,
            remapForSkinning,
            unknown,
            skinnedJoints,
            extraJoints,
            defaultBounds,
            numLods,
            maxLodDeviations,
            blendShapeNames,
            unknown1,
            unknown2,
            unknown3,
            jointBoundRadius
        );
    }
}
