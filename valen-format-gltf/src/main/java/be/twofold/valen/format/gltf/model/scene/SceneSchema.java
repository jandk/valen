package be.twofold.valen.format.gltf.model.scene;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.node.*;
import org.immutables.value.*;

import java.util.*;

/**
 * The root nodes of a scene.
 */
@SchemaStyle
@Value.Immutable
public interface SceneSchema extends GltfChildOfRootProperty {

    /**
     * The indices of each root node.
     */
    @Value.NaturalOrder
    SortedSet<NodeID> getNodes();

}
