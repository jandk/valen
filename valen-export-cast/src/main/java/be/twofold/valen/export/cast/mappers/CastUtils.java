package be.twofold.valen.export.cast.mappers;

import be.twofold.tinycast.*;
import wtf.reversed.toolbox.math.*;

public final class CastUtils {
    private CastUtils() {
    }

    public static Vec3 mapVector3(Vector3 vector) {
        return new Vec3(
            vector.x(),
            vector.y(),
            vector.z()
        );
    }

    public static Vec4 mapVector4(Vector4 vector) {
        return new Vec4(
            vector.x(),
            vector.y(),
            vector.z(),
            vector.w()
        );
    }

    public static Vec4 mapQuaternion(Quaternion quaternion) {
        return new Vec4(
            quaternion.x(),
            quaternion.y(),
            quaternion.z(),
            quaternion.w()
        );
    }
}
