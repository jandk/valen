package be.twofold.valen.gltf.model;

import java.util.*;

public interface GltfChildOfRootProperty extends GltfProperty {
    /**
     * The user-defined name of this object.
     */
    Optional<String> getName();
}
