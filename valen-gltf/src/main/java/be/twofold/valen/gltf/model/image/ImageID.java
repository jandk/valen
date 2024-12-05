package be.twofold.valen.gltf.model.image;

import be.twofold.valen.gltf.model.*;

public final class ImageID extends GltfID {
    private ImageID(int id) {
        super(id);
    }

    public static ImageID of(int id) {
        return new ImageID(id);
    }
}
