package be.twofold.valen.format.gltf.model.camera;

import be.twofold.valen.format.gltf.model.*;

public final class CameraID extends GltfID {
    private CameraID(int id) {
        super(id);
    }

    public static CameraID of(int id) {
        return new CameraID(id);
    }
}
