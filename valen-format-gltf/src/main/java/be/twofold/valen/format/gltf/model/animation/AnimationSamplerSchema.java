package be.twofold.valen.format.gltf.model.animation;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.accessor.*;
import org.immutables.value.*;

import java.util.*;

/**
 * An animation sampler combines timestamps with a sequence of output values and defines an interpolation algorithm.
 */
@SchemaStyle
@Value.Immutable
public interface AnimationSamplerSchema extends GltfProperty {

    /**
     * The index of an accessor containing keyframe timestamps. (Required)
     */
    AccessorID getInput();

    /**
     * Interpolation algorithm.
     */
    Optional<AnimationSamplerInterpolation> getInterpolation();

    /**
     * The index of an accessor, containing keyframe output values. (Required)
     */
    AccessorID getOutput();

}
