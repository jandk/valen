package be.twofold.valen.export.gltf.model;

import org.immutables.value.*;

/**
 * An animation sampler combines timestamps with a sequence of output values and defines an interpolation algorithm.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface AnimationSamplerDef extends PropertyDef {
    /**
     * The index of the accessor containing keyframe timestamps.
     */
    AccessorId getInput();

    /**
     * Interpolation algorithm.
     */
    AnimationSamplerInterpolation getInterpolation();

    /**
     * The index of the accessor containing keyframe output values.
     */
    AccessorId getOutput();
}
