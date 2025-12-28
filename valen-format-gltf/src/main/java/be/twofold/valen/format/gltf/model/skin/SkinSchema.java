package be.twofold.valen.format.gltf.model.skin;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.accessor.*;
import be.twofold.valen.format.gltf.model.node.*;
import org.immutables.value.*;

import java.util.*;

/**
 * Joints and matrices defining a skin.
 */
@SchemaStyle
@Value.Immutable
public interface SkinSchema extends GltfChildOfRootProperty {

    /**
     * The index of the accessor containing the floating-point 4x4 inverse-bind matrices.
     */
    Optional<AccessorID> getInverseBindMatrices();

    /**
     * The index of the node used as a skeleton root.
     */
    Optional<NodeID> getSkeleton();

    /**
     * Indices of skeleton nodes, used as joints in this skin. (Required)
     */
    @Value.NaturalOrder
    SortedSet<NodeID> getJoints();

}
