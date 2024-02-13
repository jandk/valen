package be.twofold.valen.export.gltf.model;

import com.google.gson.*;

import java.util.*;

public interface PropertyDef {
    /**
     * JSON object with extension-specific objects.
     */
    Optional<JsonObject> getExtensions();

    /**
     * Application-specific data.
     */
    Optional<JsonObject> getExtras();
}
