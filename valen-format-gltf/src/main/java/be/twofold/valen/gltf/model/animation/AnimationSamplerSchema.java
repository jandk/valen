package be.twofold.valen.gltf.model.animation;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.accessor.*;
import org.immutables.value.*;

import java.util.*;

/**
 * An animation sampler combines timestamps with a sequence of output values and defines an interpolation algorithm.
 */
@Schema2Style
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
