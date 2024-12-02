package be.twofold.valen.gltf.model.texture;

import be.twofold.valen.gltf.model.*;

public final class TextureID extends GltfID {
    private TextureID(int id) {
        super(id);
    }

    public static TextureID of(int id) {
        return new TextureID(id);
    }
}
