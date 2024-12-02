package be.twofold.valen.gltf.model.scene;

import be.twofold.valen.gltf.model.*;

public final class SceneID extends GltfID {
    private SceneID(int id) {
        super(id);
    }

    public static SceneID of(int id) {
        return new SceneID(id);
    }
}
