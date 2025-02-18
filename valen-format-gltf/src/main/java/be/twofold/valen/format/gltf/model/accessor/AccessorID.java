package be.twofold.valen.format.gltf.model.accessor;

import be.twofold.valen.format.gltf.model.*;

public final class AccessorID extends GltfID {
    private AccessorID(int id) {
        super(id);
    }

    public static AccessorID of(int id) {
        return new AccessorID(id);
    }
}
