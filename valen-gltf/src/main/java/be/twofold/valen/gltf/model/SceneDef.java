package be.twofold.valen.gltf.model;

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
    List<NodeId> getNodes();
}
