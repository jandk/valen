package be.twofold.valen.format.gltf.model.animation;

import be.twofold.valen.format.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

/**
 * A keyframe animation.
 */
@Schema2Style
@Value.Immutable
public interface AnimationSchema extends GltfChildOfRootProperty {

    /**
     * An array of animation channels. An animation channel combines an animation sampler with a target property being
     * animated. Different channels of the same animation **MUST NOT** have the same targets. (Required)
     */
    List<AnimationChannelSchema> getChannels();

    /**
     * An array of animation samplers. An animation sampler combines timestamps with a sequence of output values and
     * defines an interpolation algorithm. (Required)
     */
    List<AnimationSamplerSchema> getSamplers();

}
