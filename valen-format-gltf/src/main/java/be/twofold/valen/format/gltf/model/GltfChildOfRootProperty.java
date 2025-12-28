package be.twofold.valen.format.gltf.model;

import java.util.*;

public interface GltfChildOfRootProperty extends GltfProperty {
    /**
     * The user-defined name of this object.
     */
    Optional<String> getName();
}
