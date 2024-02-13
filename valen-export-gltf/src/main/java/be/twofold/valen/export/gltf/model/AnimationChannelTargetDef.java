package be.twofold.valen.export.gltf.model;

import org.immutables.value.*;

import java.util.*;

/**
 * The descriptor of the animated property.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface AnimationChannelTargetDef extends GltfProperty {
    /**
     * The index of the node to animate.
     * <p>
     * When undefined, the animated object MAY be defined by an extension.
     */
    Optional<NodeId> getNode();

    /**
     * The name of the node’s TRS property to animate, or the "weights" of the Morph Targets it instantiates.
     *
     * <ul>
     *     <li>For the "translation" property, the values that are provided by the sampler are the translation along the X, Y, and Z axes.</li>
     *     <li>For the "rotation" property, the values are a quaternion in the order (x, y, z, w), where w is the scalar.</li>
     *     <li>For the "scale" property, the values are the scaling factors along the X, Y, and Z axes.</li>
     * </ul>
     */
    AnimationChannelTargetPath getPath();
}
