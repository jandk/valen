package be.twofold.valen.export.gltf.model;

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
