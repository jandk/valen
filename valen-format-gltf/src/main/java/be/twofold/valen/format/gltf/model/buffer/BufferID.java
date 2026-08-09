package be.twofold.valen.format.gltf.model.buffer;

import be.twofold.valen.format.gltf.model.*;

public final class BufferID extends GltfID {
    private BufferID(int id) {
        super(id);
    }

    public static BufferID of(int id) {
        return new BufferID(id);
    }
}
