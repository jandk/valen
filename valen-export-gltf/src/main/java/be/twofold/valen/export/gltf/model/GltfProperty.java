package be.twofold.valen.export.gltf.model;

import com.google.gson.*;

import java.util.*;

public interface GltfProperty {
    /**
     * JSON object with extension-specific objects.
     */
    Map<String, Extension> getExtensions();

    /**
     * Application-specific data.
     */
    Optional<JsonObject> getExtras();
}
