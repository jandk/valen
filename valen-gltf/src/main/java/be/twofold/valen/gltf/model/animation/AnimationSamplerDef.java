package be.twofold.valen.gltf.model.animation;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.accessor.*;
import org.immutables.value.*;

import java.util.*;

/**
 * An animation sampler combines timestamps with a sequence of output values and defines an interpolation algorithm.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface AnimationSamplerDef extends GltfProperty {
    /**
     * The index of the accessor containing keyframe timestamps.
     */
    AccessorID getInput();

    /**
     * Interpolation algorithm.
     */
    Optional<AnimationSamplerInterpolation> getInterpolation();

    /**
     * The index of the accessor containing keyframe output values.
     */
    AccessorID getOutput();
}
