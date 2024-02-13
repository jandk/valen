package be.twofold.valen.export.gltf.model;

import org.immutables.value.*;

import java.util.*;

/**
 * Joints and matrices defining a skin.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface SkinDef extends GltfChildOfRootProperty {
    /**
     * The index of the accessor containing the floating-point 4x4 inverse-bind matrices.
     */
    Optional<AccessorId> getInverseBindMatrices();

    /**
     * The indices of the joint nodes.
     */
    List<NodeId> getJoints();

    /**
     * The index of the node used as a skeleton root.
     */
    Optional<NodeId> getSkeleton();
}
