package be.twofold.valen.gltf.model;

import org.immutables.value.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface AnimationChannelDef extends GltfProperty {
    /**
     * The index of a sampler in this animation used to compute the value for the target.
     */
    AnimationSamplerId getSampler();

    /**
     * The index of the node and TRS property to target.
     */
    AnimationChannelTargetSchema getTarget();
}
