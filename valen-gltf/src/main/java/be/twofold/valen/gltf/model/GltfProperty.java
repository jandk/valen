package be.twofold.valen.gltf.model;

import be.twofold.valen.gltf.model.extension.*;
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
