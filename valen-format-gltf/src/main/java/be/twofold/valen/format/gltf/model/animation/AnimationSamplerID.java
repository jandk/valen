package be.twofold.valen.format.gltf.model.animation;

import be.twofold.valen.format.gltf.model.*;

// NOTE: Technically not a GltfID
public final class AnimationSamplerID extends GltfID {
    private AnimationSamplerID(int id) {
        super(id);
    }

    public static AnimationSamplerID of(int id) {
        return new AnimationSamplerID(id);
    }
}
