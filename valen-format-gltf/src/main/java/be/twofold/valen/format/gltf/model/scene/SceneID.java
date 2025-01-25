package be.twofold.valen.format.gltf.model.scene;

import be.twofold.valen.format.gltf.model.*;

public final class SceneID extends GltfID {
    private SceneID(int id) {
        super(id);
    }

    public static SceneID of(int id) {
        return new SceneID(id);
    }
}
