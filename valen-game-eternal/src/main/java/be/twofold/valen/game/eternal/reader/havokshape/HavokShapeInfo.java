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
    public static HavokShapeInfo read(BinaryReader reader) throws IOException {
        var contentFlags = reader.readInt();
        var surfaceType = reader.readInt();
        var surfaceFlags = reader.readInt();
        var surfaceVelocityGroup = reader.readInt();
        var dynamicFriction = reader.readFloat();
        var staticFriction = reader.readFloat();
        var restitution = reader.readFloat();
        var unknown = reader.readInt();

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
