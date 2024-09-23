package be.twofold.valen.gltf.model.animation;

import be.twofold.valen.gltf.model.*;

public enum AnimationSamplerInterpolation implements ValueEnum<String> {
    LINEAR,
    STEP,
    CUBICSPLINE;

    @Override
    public String value() {
        return name();
    }
}
