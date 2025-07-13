package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.BinaryReader;
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
    public static Md6ModelHeader read(BinaryReader reader, int numJoints8) throws IOException {
        var minBoundsExpansion = Vector3.read(reader);
        var maxBoundsExpansion = Vector3.read(reader);
        var remapForSkinning = reader.readBoolByte();
        var unknown = reader.readPString();
        var skinnedJoints = reader.readShorts(Short.toUnsignedInt(reader.readShort()));
        var extraJoints = reader.readShorts(Short.toUnsignedInt(reader.readShort()));
        var defaultBounds = Bounds.read(reader);
        var numLods = reader.readInt();
        var maxLodDeviations = reader.readFloats(5);
        var blendShapeNames = reader.readObjects(reader.readInt(), BinaryReader::readPString);
        var unknown1 = Vector3.read(reader);
        var unknown2 = Vector3.read(reader);
        var unknown3 = Vector3.read(reader);
        var jointBoundRadius = reader.readFloats(numJoints8);

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
