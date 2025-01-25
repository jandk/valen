package be.twofold.valen.format.gltf.model.bufferview;

import be.twofold.valen.format.gltf.model.*;

public final class BufferViewID extends GltfID {
    private BufferViewID(int id) {
        super(id);
    }

    public static BufferViewID of(int id) {
        return new BufferViewID(id);
    }
}
