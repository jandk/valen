package be.twofold.valen.format.gltf.model.sampler;

import be.twofold.valen.format.gltf.model.*;

public final class SamplerID extends GltfID {
    private SamplerID(int id) {
        super(id);
    }

    public static SamplerID of(int id) {
        return new SamplerID(id);
    }
}
