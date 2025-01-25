package be.twofold.valen.format.gltf.model.material;

import be.twofold.valen.format.gltf.model.*;

public final class MaterialID extends GltfID {
    private MaterialID(int id) {
        super(id);
    }

    public static MaterialID of(int id) {
        return new MaterialID(id);
    }
}
