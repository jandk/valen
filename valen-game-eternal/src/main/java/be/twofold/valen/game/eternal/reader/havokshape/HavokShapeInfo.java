package be.twofold.valen.game.eternal.reader.havokshape;

import be.twofold.valen.core.io.*;

import java.io.*;

public record HavokShapeInfo(
    int contentFlags,
    int surfaceType,
    int surfaceFlags,
    int surfaceVelocityGroup,
    float dynamicFriction,
    float staticFriction,
    float restitution,
    int unknown
) {
    public static HavokShapeInfo read(BinarySource source) throws IOException {
        var contentFlags = source.readInt();
        var surfaceType = source.readInt();
        var surfaceFlags = source.readInt();
        var surfaceVelocityGroup = source.readInt();
        var dynamicFriction = source.readFloat();
        var staticFriction = source.readFloat();
        var restitution = source.readFloat();
        var unknown = source.readInt();

        return new HavokShapeInfo(
            contentFlags,
            surfaceType,
            surfaceFlags,
            surfaceVelocityGroup,
            dynamicFriction,
            staticFriction,
            restitution,
            unknown
        );
    }
}
