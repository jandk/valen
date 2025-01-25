package be.twofold.valen.gltf.model.mesh;

import be.twofold.valen.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

/**
 * A set of primitives to be rendered.  Its global transform is defined by a node that references it.
 */
@Schema2Style
@Value.Immutable
public interface MeshSchema extends GltfChildOfRootProperty {

    /**
     * An array of primitives, each defining geometry to be rendered. (Required)
     */
    List<MeshPrimitiveSchema> getPrimitives();

    /**
     * Array of weights to be applied to the morph targets. The number of array elements <b>MUST</b> match the number of
     * morph targets.
     */
    Optional<float[]> getWeights();

}
