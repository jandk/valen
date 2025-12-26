package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.math.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Md6ModelHeader(
    Vector3 minBoundsExpansion,
    Vector3 maxBoundsExpansion,
    boolean remapForSkinning,
    String unknown,
    Shorts skinnedJoints,
    Shorts extraJoints,
    Bounds defaultBounds,
    int numLods,
    Floats maxLodDeviations,
    List<String> blendShapeNames,
    Vector3 unknown1,
    Vector3 unknown2,
    Vector3 unknown3,
    Floats jointBoundRadius
) {
    public static Md6ModelHeader read(BinarySource source, int numJoints8) throws IOException {
        var minBoundsExpansion = Vector3.read(source);
        var maxBoundsExpansion = Vector3.read(source);
        var remapForSkinning = source.readBool(BoolFormat.BYTE);
        var unknown = source.readString(StringFormat.INT_LENGTH);
        var skinnedJoints = source.readShorts(source.readShort());
        var extraJoints = source.readShorts(source.readShort());
        var defaultBounds = Bounds.read(source);
        var numLods = source.readInt();
        var maxLodDeviations = source.readFloats(5);
        var blendShapeNames = source.readStrings(source.readInt(), StringFormat.INT_LENGTH);
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
