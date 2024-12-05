package be.twofold.valen.gltf.model.mesh;

import be.twofold.valen.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface MeshDef extends GltfChildOfRootProperty {
    /**
     * An array of primitives, each defining geometry to be rendered.
     */
    List<MeshPrimitiveSchema> getPrimitives();

    /**
     * Array of weights to be applied to the morph targets.
     */
    Optional<float[]> getWeights();
}
