package be.twofold.valen.format.gltf.model.node;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.camera.*;
import be.twofold.valen.format.gltf.model.mesh.*;
import be.twofold.valen.format.gltf.model.skin.*;
import be.twofold.valen.format.gltf.types.*;
import org.immutables.value.*;

import java.util.*;

/**
 * A node in the node hierarchy.  When the node contains {@code skin}, all {@code mesh.primitives} <b>MUST</b> contain
 * {@code JOINTS_0} and {@code WEIGHTS_0} attributes.  A node <b>MAY</b> have either a {@code matrix} or any combination
 * of {@code translation}/{@code rotation}/{@code scale} (TRS) properties. TRS properties are converted to matrices and
 * postmultiplied in the {@code T * R * S} order to compose the transformation matrix; first the scale is applied to the
 * vertices, then the rotation, and then the translation. If none are provided, the transform is the identity. When a
 * node is targeted for animation (referenced by an animation.channel.target), {@code matrix} **MUST NOT** be present.
 */
@SchemaStyle
@Value.Immutable
public interface NodeSchema extends GltfChildOfRootProperty {

    /**
     * The index of the camera referenced by this node.
     */
    Optional<CameraID> getCamera();

    /**
     * The indices of this node's children.
     */
    @Value.NaturalOrder
    SortedSet<NodeID> getChildren();

    /**
     * The index of the skin referenced by this node.
     */
    Optional<SkinID> getSkin();

    /**
     * A floating-point 4x4 transformation matrix stored in column-major order.
     */
    Optional<Mat4> getMatrix();

    /**
     * The index of the mesh in this node.
     */
    Optional<MeshID> getMesh();

    /**
     * The node's unit quaternion rotation in the order (x, y, z, w), where w is the scalar.
     */
    Optional<Vec4> getRotation();

    /**
     * The node's non-uniform scale, given as the scaling factors along the x, y, and z axes.
     */
    Optional<Vec3> getScale();

    /**
     * The node's translation along the x, y, and z axes.
     */
    Optional<Vec3> getTranslation();

    /**
     * The weights of the instantiated morph target. The number of array elements <b>MUST</b> match the number of morph
     * targets of the referenced mesh. When defined, {@code mesh} <b>MUST</b> also be defined.
     */
    Optional<float[]> getWeights();

}
