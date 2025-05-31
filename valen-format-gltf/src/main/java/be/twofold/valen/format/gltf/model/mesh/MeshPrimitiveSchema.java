package be.twofold.valen.format.gltf.model.mesh;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.accessor.*;
import be.twofold.valen.format.gltf.model.material.*;
import org.immutables.value.*;

import java.util.*;

/**
 * Geometry to be rendered with the given material.
 */
@Schema2Style
@Value.Immutable
public interface MeshPrimitiveSchema extends GltfProperty {

    /**
     * A plain JSON object, where each key corresponds to a mesh attribute semantic and each value is the index of the
     * accessor containing attribute's data. (Required)
     */
    Map<String, AccessorID> getAttributes();

    /**
     * The index of the accessor that contains the vertex indices.
     */
    Optional<AccessorID> getIndices();

    /**
     * The index of the material to apply to this primitive when rendering.
     */
    Optional<MaterialID> getMaterial();

    /**
     * The topology type of primitives to render.
     */
    Optional<MeshPrimitiveMode> getMode();

    /**
     * An array of morph targets.
     */
    Optional<List<Map<String, AccessorID>>> getTargets();

}
