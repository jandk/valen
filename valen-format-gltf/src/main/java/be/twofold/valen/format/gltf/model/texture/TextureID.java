package be.twofold.valen.format.gltf.model.texture;

import be.twofold.valen.format.gltf.model.*;

public final class TextureID extends GltfID {
    private TextureID(int id) {
        super(id);
    }

    public static TextureID of(int id) {
        return new TextureID(id);
    }
}
