package be.twofold.valen.export.gltf.model;

import be.twofold.valen.core.math.*;
import org.immutables.value.*;

import java.util.*;

/**
 * A node in the node hierarchy.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface NodeDef extends GltfChildOfRootProperty {
    /**
     * The index of the camera referenced by this node.
     */
    Optional<CameraId> getCamera();

    /**
     * The indices of this node’s children.
     */
    List<NodeId> getChildren();

    /**
     * The index of the skin referenced by this node.
     */
    Optional<SkinId> getSkin();

    /**
     * A floating-point 4x4 transformation matrix stored in column-major order.
     */
    Optional<Matrix4> getMatrix();

    /**
     * The index of the mesh in this node.
     */
    Optional<MeshId> getMesh();

    /**
     * The node’s unit quaternion rotation in the order (x, y, z, w), where w is the scalar.
     */
    Optional<Quaternion> getRotation();

    /**
     * The node’s non-uniform scale.
     */
    Optional<Vector3> getScale();

    /**
     * The node’s translation.
     */
    Optional<Vector3> getTranslation();

    /**
     * The weights of the instantiated Morph Target.
     */
    Optional<float[]> getWeights();
}
