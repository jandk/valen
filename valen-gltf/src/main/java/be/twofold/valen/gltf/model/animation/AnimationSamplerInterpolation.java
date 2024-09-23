package be.twofold.valen.gltf.model.animation;

import be.twofold.valen.gltf.model.*;

public enum AnimationSamplerInterpolation implements ValueEnum<String> {
    LINEAR("LINEAR"),
    STEP("STEP"),
    CUBIC_SPLINE("CUBICSPLINE");

    private final String value;

    AnimationSamplerInterpolation(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
