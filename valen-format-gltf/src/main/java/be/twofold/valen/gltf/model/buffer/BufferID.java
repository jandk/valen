package be.twofold.valen.gltf.model.buffer;

import be.twofold.valen.gltf.model.*;

public final class BufferID extends GltfID {
    public static BufferID ZERO = of(0);

    private BufferID(int id) {
        super(id);
    }

    public static BufferID of(int id) {
        return new BufferID(id);
    }
}
