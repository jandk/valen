package be.twofold.valen.gltf.model.scene;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.node.*;
import org.immutables.value.*;

import java.util.*;

/**
 * The root nodes of a scene.
 */
@Schema2Style
@Value.Immutable
public interface SceneSchema extends GltfChildOfRootProperty {

    /**
     * The indices of each root node.
     */
    @Value.NaturalOrder
    SortedSet<NodeID> getNodes();

}
