package be.twofold.valen.gltf.model.animation;

public enum AnimationSamplerInterpolation {
    Linear("LINEAR"),
    Step("STEP"),
    CubicSpline("CUBICSPLINE");

    private final String value;

    AnimationSamplerInterpolation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
