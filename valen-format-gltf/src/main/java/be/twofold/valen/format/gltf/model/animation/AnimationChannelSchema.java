package be.twofold.valen.format.gltf.model.animation;

import be.twofold.valen.format.gltf.model.*;
import org.immutables.value.*;

/**
 * An animation channel combines an animation sampler with a target property being animated.
 */
@SchemaStyle
@Value.Immutable
public interface AnimationChannelSchema extends GltfProperty {

    /**
     * The index of a sampler in this animation used to compute the value for the target. (Required)
     */
    AnimationSamplerID getSampler();

    /**
     * The descriptor of the animated property. (Required)
     */
    AnimationChannelTargetSchema getTarget();

}
