package be.twofold.valen.gltf.model.buffer;

import be.twofold.valen.gltf.model.*;

public final class BufferViewID extends GltfID {
    private BufferViewID(int id) {
        super(id);
    }

    public static BufferViewID of(int id) {
        return new BufferViewID(id);
    }
}
