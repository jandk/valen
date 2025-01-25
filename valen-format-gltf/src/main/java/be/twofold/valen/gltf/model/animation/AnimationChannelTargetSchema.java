package be.twofold.valen.gltf.model.animation;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.node.*;
import org.immutables.value.*;

import java.util.*;

/**
 * The descriptor of the animated property.
 */
@Schema2Style
@Value.Immutable
public interface AnimationChannelTargetSchema extends GltfProperty {

    /**
     * The index of the node to animate. When undefined, the animated object <b>MAY</b> be defined by an extension.
     */
    Optional<NodeID> getNode();

    /**
     * The name of the node's TRS property to animate, or the {@code "weights"} of the Morph Targets it instantiates.
     * For the {@code "translation"} property, the values that are provided by the sampler are the translation along the
     * X, Y, and Z axes. For the {@code "rotation"} property, the values are a quaternion in the order (x, y, z, w),
     * where w is the scalar. For the {@code "scale"} property, the values are the scaling factors along the X, Y, and Z
     * axes. (Required)
     */
    AnimationChannelTargetPath getPath();

}
