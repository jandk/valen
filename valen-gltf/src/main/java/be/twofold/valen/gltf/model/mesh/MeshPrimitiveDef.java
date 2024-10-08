package be.twofold.valen.gltf.model.mesh;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.accessor.*;
import be.twofold.valen.gltf.model.material.*;
import com.google.gson.*;
import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface MeshPrimitiveDef extends GltfProperty {
    /**
     * A plain JSON object, where each key corresponds to a mesh attribute semantic
     * and each value is the index of the accessor containing attribute’s data.
     */
    JsonObject getAttributes();

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
     * An array of Morph Targets.
     */
    List<Object> getTargets();
}
