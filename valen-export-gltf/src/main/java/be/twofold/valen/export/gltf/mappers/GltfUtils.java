package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.math.*;
import be.twofold.valen.gltf.types.*;

public final class GltfUtils {
    private GltfUtils() {
    }

    public static Vec4 mapQuaternion(Quaternion q) {
        return new Vec4(q.x(), q.y(), q.z(), q.w());
    }

    public static Vec3 mapVector3(Vector3 v) {
        return new Vec3(v.x(), v.y(), v.z());
    }

    public static Vec4 mapVector4(Vector4 v) {
        return new Vec4(v.x(), v.y(), v.z(), v.w());
    }
}
