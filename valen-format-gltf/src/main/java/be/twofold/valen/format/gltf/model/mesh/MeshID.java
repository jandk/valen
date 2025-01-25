package be.twofold.valen.format.gltf.model.mesh;

import be.twofold.valen.format.gltf.model.*;

public final class MeshID extends GltfID {
    private MeshID(int id) {
        super(id);
    }

    public static MeshID of(int id) {
        return new MeshID(id);
    }
}
