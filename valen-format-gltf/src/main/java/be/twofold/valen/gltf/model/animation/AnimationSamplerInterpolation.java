package be.twofold.valen.gltf.model.animation;

import be.twofold.valen.gltf.model.*;

public enum AnimationSamplerInterpolation implements SerializableEnum<String> {
    /**
     * The animated values are linearly interpolated between keyframes. When targeting a rotation, spherical linear
     * interpolation (slerp) <b>SHOULD</b> be used to interpolate quaternions. The number of output elements <b>MUST</b>
     * equal the number of input elements.
     */
    LINEAR,

    /**
     * The animated values remain constant to the output of the first keyframe, until the next keyframe. The number of
     * output elements <b>MUST</b> equal the number of input elements.
     */
    STEP,

    /**
     * The animation's interpolation is computed using a cubic spline with specified tangents. The number of output
     * elements <b>MUST</b> equal three times the number of input elements. For each input element, the output stores
     * three elements, an in-tangent, a spline vertex, and an out-tangent. There <b>MUST</b> be at least two keyframes
     * when using this interpolation.
     */
    CUBICSPLINE;

    @Override
    public String value() {
        return name();
    }
}
