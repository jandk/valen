package be.twofold.valen.format.gltf.model;

import be.twofold.valen.format.gltf.model.extension.*;

import java.util.*;

public interface GltfProperty {
    /**
     * JSON object with extension-specific objects.
     */
    Map<String, Extension> getExtensions();

    /**
     * Application-specific data.
     */
    Optional<Object> getExtras();
}
