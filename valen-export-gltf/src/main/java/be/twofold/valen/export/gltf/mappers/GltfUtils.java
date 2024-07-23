package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.math.*;
import be.twofold.valen.gltf.types.*;

final class GltfUtils {
    private GltfUtils() {
    }

    static Vec3 mapVector3(Vector3 v) {
        return new Vec3(v.x(), v.y(), v.z());
    }

    static Vec4 mapQuaternion(Quaternion q) {
        return new Vec4(q.x(), q.y(), q.z(), q.w());
    }
}
