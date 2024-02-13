package be.twofold.valen.export.gltf.model;

import java.util.*;

public interface GltfChildOfRootProperty extends PropertyDef {
    /**
     * The user-defined name of this object.
     */
    Optional<String> getName();
}
