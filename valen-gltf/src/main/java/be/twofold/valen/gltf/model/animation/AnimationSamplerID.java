package be.twofold.valen.gltf.model.animation;

import be.twofold.valen.gltf.model.*;

public final class AnimationSamplerID extends GltfID {
    private AnimationSamplerID(int id) {
        super(id);
    }

    public static AnimationSamplerID of(int id) {
        return new AnimationSamplerID(id);
    }
}
