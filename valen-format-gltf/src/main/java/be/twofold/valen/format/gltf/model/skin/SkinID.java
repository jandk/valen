package be.twofold.valen.format.gltf.model.skin;

import be.twofold.valen.format.gltf.model.*;

public final class SkinID extends GltfID {
    private SkinID(int id) {
        super(id);
    }

    public static SkinID of(int id) {
        return new SkinID(id);
    }
}
