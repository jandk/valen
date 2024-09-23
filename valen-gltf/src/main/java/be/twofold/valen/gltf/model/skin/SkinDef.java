package be.twofold.valen.gltf.model.skin;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.accessor.*;
import be.twofold.valen.gltf.model.node.*;
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
    Optional<AccessorID> getInverseBindMatrices();

    /**
     * The indices of the joint nodes.
     */
    List<NodeID> getJoints();

    /**
     * The index of the node used as a skeleton root.
     */
    Optional<NodeID> getSkeleton();
}
