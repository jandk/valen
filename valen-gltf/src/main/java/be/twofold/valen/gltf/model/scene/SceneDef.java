package be.twofold.valen.gltf.model.scene;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.node.*;
import org.immutables.value.*;

import java.util.*;

/**
 * Thr root nodes of a scene.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface SceneDef extends GltfChildOfRootProperty {
    /**
     * The indices of each root node.
     */
    List<NodeID> getNodes();
}
